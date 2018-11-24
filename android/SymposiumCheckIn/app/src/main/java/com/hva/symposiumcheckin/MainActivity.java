package com.hva.symposiumcheckin;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hva.symposiumcheckin.database.DatabaseConnection;
import com.hva.symposiumcheckin.database.DatabaseHelper;
import com.hva.symposiumcheckin.fragment.AddStudentDatabaseDialogFragment;
import com.hva.symposiumcheckin.fragment.CheckInStudentNumberDialogFragment;
import com.hva.symposiumcheckin.fragment.ConnectToDatabaseDialogFragment;

import java.text.MessageFormat;
import java.time.*;
import java.time.format.*;
import java.util.*;

//TODO: Stop app from crashing on first startup
//TODO: This only occurs during first startup, so not that important to fix.
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Intent that is opened when NFC has scanned a card
    private PendingIntent mPendingIntent;
    // Represents NFC adapter
    private NfcAdapter mNfcAdapter;

    // Dialog that is seen before updating the 'bedrijfspunten' table
    private Dialog updateBedrijfspuntenDialog;

    // String in which the last student card credential is saved, for when someone accidentally scans two times in a row
    private String mLastStudentCardSerial;

    // TextViews
    private TextView nfcStatusView;
    private TextView dbConnectionStatus;
    private TextView checkInStatusView;
    private TextView dbDataContainer;

    // ImageView
    private ImageView scanNFCImage;

    // Database helper for getting information
    public DatabaseHelper dbHelper;

    // Boolean to check if the Student check in fragment is already open
    public boolean checkInFragmentOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set last student credential to empty string
        mLastStudentCardSerial = "";

        setView();

        getNFCReader();

        dbHelper = new DatabaseHelper(this);

        // Set a reference to the this activity in DatabaseConnection
        DatabaseConnection.getInstance().setMainActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.commonmenus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.mnuConnect){
            showConnectToDatabaseFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the different views in mainActivity to variables. It also sets on click listeners.
     */
    private void setView() {
        nfcStatusView = findViewById(R.id.nfcStatusView);
        scanNFCImage = findViewById(R.id.scanNFCImage);
        checkInStatusView = findViewById(R.id.checkInStatusView);
        dbConnectionStatus = findViewById(R.id.dbConnectionStatusView);
        dbDataContainer = findViewById(R.id.dbActionsContainer);

        findViewById(R.id.updateBedrijfspuntenButton).setOnClickListener(this);
        findViewById(R.id.checkInViaStudentNumberButton).setOnClickListener(this);
    }

    /**
     * Get the NFC adapter, by using getDefaultAdapter.
     * Also changes a text view to say the state of NFC.
     * Three states:
     * - No NFC in device
     * - NFC is turned off
     * - NFC is turned on
     */
    private void getNFCReader() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) { // If there is no default adapter, there is no NFC in the device, so you can't fully use this app
            nfcStatusView.setText(R.string.nfc_not_supported);
            nfcStatusView.setTextColor(Color.RED);
            Toast.makeText(this, getString(R.string.nfc_not_supported_msg), Toast.LENGTH_LONG).show();
        } else if (!mNfcAdapter.isEnabled()) { // If you have a NFC adapter but it is not on, ask to turn it on
            nfcStatusView.setText(R.string.nfc_disabled);
            nfcStatusView.setTextColor(Color.RED);
            Toast.makeText(this, getString(R.string.turn_nfc_on_msg), Toast.LENGTH_LONG).show();
        } else { // NFC is on so change the NFC text to on
            nfcStatusView.setText(R.string.nfc_enabled);
            nfcStatusView.setTextColor(Color.GREEN);
            // Set the pending intent, Single top to have only one intent on top
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
    }

    /**
     * Get connection to the database, if there is no connection, the database is offline. Or the user is not online.
     * Also sets the database view to if database is connected or not.
     */
    private void getDbConnection() {
        boolean isConnected = dbHelper.getConnection();
        setUiAfterDatabaseConnection(isConnected);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Disable NFC for this application because app is paused
        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enable the NFC functionality for this application in the foreground because application is resumed
        if (mPendingIntent != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }

        checkInStatusView.setVisibility(View.GONE);
        scanNFCImage.setVisibility(View.VISIBLE);

        getNFCReader();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getTagId(intent);
    }

    /**
     * Function to get the tag id from nfc adapter, but also gets student number and if it is not present in database.
     * Opens a fragment to add the card id to the database.
     *
     * @param intent Intent to get NFC adapter information
     */
    private void getTagId(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        // Get tag id and from there get student credential. Turn bytes to integer, and than set it to a string.
        StringBuilder studentCardSerial = new StringBuilder();
        for (byte byteKey : tag.getId()) {
            int x = (0x000000FF & byteKey);  // byte to int conversion
            String s = Integer.toHexString(x).toUpperCase();
            if (s.length() == 1) {
                s = "0" + s;
            }
            studentCardSerial.insert(0, s + "");
        }

        if (!studentCardSerial.toString().equals(mLastStudentCardSerial)) {
            String studentNumber = dbHelper.getStudentNumber(studentCardSerial);
            switch (studentNumber) {
                case "student_number_not_found":
                    // When studentNumber is null we have to add this to the db
                    setUiStudentNotInDatabase();
                    showAddStudentDialogFragment(studentCardSerial.toString());
                    break;
                case "no_internet_connection":
                    // No internet connection, toast is shown
                    break;
                default:
                    // Student number is found and we can check it in
                    dbHelper.checkStudentIn(studentNumber);
                    break;
            }
            mLastStudentCardSerial = studentCardSerial.toString();
        } else { // Say that this was already scanned
            Toast.makeText(this, getString(R.string.student_already_scanned), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Set different views to show that student is checked in.
     *
     * @param studentNumber student number that is checked in
     */
    public void setUiStudentCheckedIn(String studentNumber) {
        checkInStatusView.setText(MessageFormat.format(getString(R.string.student_card_checked_in), studentNumber));
        checkInStatusView.setTextColor(Color.GREEN);

        checkInStatusView.setVisibility(View.VISIBLE);

    }

    /**
     * Set view so that users know this student is not in the database.
     */
    private void setUiStudentNotInDatabase() {
        checkInStatusView.setText(R.string.student_card_not_registered);
        checkInStatusView.setTextColor(Color.RED);

        scanNFCImage.setVisibility(View.GONE);
        checkInStatusView.setVisibility(View.VISIBLE);
    }

    /**
     * Fragment to add user to database through the student card serial
     *
     * @param studentCardSerial Serial that is going to be linked to inserted studentNumber
     */
    private void showAddStudentDialogFragment(String studentCardSerial) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("addStudentDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment addStudentFragment = AddStudentDatabaseDialogFragment.newInstance(studentCardSerial);
        addStudentFragment.setCancelable(false);
        addStudentFragment.show(ft, "addStudentDialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateBedrijfspuntenButton:
                makeAlertDialogUpdateBedrijfsPunten();
                break;
            case R.id.checkInViaStudentNumberButton:
                showAddStudentNumberDialogFragment();
        }
    }

    /**
     * Alert dialog that is opened, before the 'bedrijfspunten' table is updated
     */
    private void makeAlertDialogUpdateBedrijfsPunten() {
        if (updateBedrijfspuntenDialog != null && updateBedrijfspuntenDialog.isShowing()) return;
        // Build alert dialog and set alert for executing this task
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.update_bedrijfspunten));
        alert.setMessage(getString(R.string.conformation_updating_bedrijfspunten));
        // When clicked on positive button the 'bedrijfspunten' table is updated
        alert.setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.updateBedrijfspuntenTable();
            }
        });

        // When negative button is clicked the dialog is dismissed
        alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the alert
        updateBedrijfspuntenDialog = alert.create();
        updateBedrijfspuntenDialog.show();
    }

    /**
     * Show the fragment to add a student through the student number, for when a student forgot his/her student card
     */
    private void showAddStudentNumberDialogFragment() {
        if (checkInFragmentOpen) return;
        checkInFragmentOpen = true;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("addViaStudentNumberDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment addStudentFragment = CheckInStudentNumberDialogFragment.newInstance();
        addStudentFragment.setCancelable(false);
        addStudentFragment.show(ft, "addViaStudentNumberDialog");
    }

    private void showConnectToDatabaseFragment() {
        if (checkInFragmentOpen) return;
        checkInFragmentOpen = true;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("tag");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment connectToDatabase = ConnectToDatabaseDialogFragment.newInstance();
        connectToDatabase.setCancelable(false);
        connectToDatabase.show(ft, "tag");
    }

    /**
     * Set view for when there is a database connection and if there is not.
     *
     * @param hasDatabaseConnection boolean that is true when connected to the database
     */
    public void setUiAfterDatabaseConnection(boolean hasDatabaseConnection) {
        if (hasDatabaseConnection) { // Set views for when connected to database
            dbConnectionStatus.setText(R.string.db_connected);
            dbConnectionStatus.setTextColor(Color.GREEN);
            dbConnectionStatus.setVisibility(View.VISIBLE);
            dbHelper.checkForTables();
        } else {
            dbConnectionStatus.setText(R.string.db_not_connected);
            dbConnectionStatus.setTextColor(Color.RED);
            dbConnectionStatus.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This function adds a string to the container that shows database changes
     *
     * @param dbStatusString The string that is added to the container view
     */

    //TODO: Append current time to message.
    //TODO: Make message box scrollable, so all messages can be viewed.
    public void setDbContainerData(String dbStatusString) {
        dbDataContainer.append(dbStatusString + "\n");
    }
}
