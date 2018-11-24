package com.hva.symposiumcheckin.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hva.symposiumcheckin.MainActivity;
import com.hva.symposiumcheckin.R;
import com.hva.symposiumcheckin.database.DatabaseConnection;

public class ConnectToDatabaseDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button btnConnectToDatabase, btnCancel;
    private EditText etUsername,etPassword, etDbName;

    // Reference to MainActivity
    private MainActivity mContext;

    // Reference to DatabaseConnection
    private DatabaseConnection dbInstance = DatabaseConnection.getInstance();

    public static ConnectToDatabaseDialogFragment newInstance() {
        ConnectToDatabaseDialogFragment fragment = new ConnectToDatabaseDialogFragment();

        // We need the student card serial in this fragment.
        // Therefor we bundle a string when we opened this fragment.
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // We need a reference to the main activity, we get this in onAttach
        // When a developer opens this fragment from a wrong class, the exception is thrown
        if (context instanceof MainActivity) {
            mContext = (MainActivity) context;
        } else {
            throw new IllegalStateException("Start this function from the MainActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_to_database_fragment, container, false);
        etDbName = (EditText) view.findViewById(R.id.etDatabaseName);
        etUsername = (EditText) view.findViewById(R.id.etDatabaseUsername);
        etPassword = (EditText) view.findViewById(R.id.etDatabasePassword);

        btnCancel = view.findViewById(R.id.cancel_connecting_to_database);
        btnCancel.setOnClickListener(this);

        btnConnectToDatabase = view.findViewById(R.id.connect_to_database);
        btnConnectToDatabase.setOnClickListener(this);

        // Set userinfo: gathered from DatabaseConnection
        etDbName.setText(dbInstance.getDbName());
        etUsername.setText(dbInstance.getUsername());
        etPassword.setText(dbInstance.getPassword());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_connecting_to_database:
                dismiss();
                break;
            case R.id.connect_to_database:
                if(dbInstance.setUserInfo(etDbName.getText().toString(), etUsername.getText().toString(),etPassword.getText().toString())){
                    if(dbInstance.getStatus()) {
                        dismiss();
                    }
                }else{
                    Toast.makeText(mContext, "Incorrecte combinatie van Databasenaam, gebruikersnaam en wachtwoord.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // When the fragment is dismissed it is closed, so change boolean to false
        mContext.checkInFragmentOpen = false;
    }
}