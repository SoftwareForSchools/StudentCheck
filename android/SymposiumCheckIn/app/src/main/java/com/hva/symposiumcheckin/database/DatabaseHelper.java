package com.hva.symposiumcheckin.database;

import android.util.Log;
import android.widget.Toast;

import com.hva.symposiumcheckin.MainActivity;
import com.hva.symposiumcheckin.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jelle on 23-1-2018.
 */

public class DatabaseHelper {
    // TODO: Add Database Information
    private static final String DB_USER_NAME = "oppenhc001";
    private static final String DB_PASSWORD = "8jsAkN4vtvG9PP";
    private static final String DB_NAME = "zoppenhc001";

    // Tables that are changed
    private static final String LOGIN_BU_TABLE_NAME = "LoginBU";
    private static final String STUDENT_CODE_TABLE_NAME = "StudentCode";
    private static final String BEDRIJFSPUNTEN_TABLE_NAME = "Bedrijfspunten";

    // There is Local for the Netherlands so we make our own
    private static final Locale NL = new Locale("nl", "NL");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", NL);

    // Connection to database
    private Connection dbConnection;

    private String mStudentNumber;

    // Reference to mainActivity
    private MainActivity mainActivity;

    public DatabaseHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private String getCurrentDateString() {
        Date dateNow = new Date();
        return DATE_FORMAT.format(dateNow);
    }

    private String getDateInTwoHours() {
        Date dateNow = new Date();
        Date endDate = new Date(dateNow.getTime() + 2 * (3600 * 1000));
        return DATE_FORMAT.format(endDate);
    }

    /**
     * Setting the UI must be done on the UI thread
     */
    private void showToast(final String stringToShow) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mainActivity, stringToShow, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Setting the UI must be done on the UI thread
     *
     * @param studentNumber Student number which is checked in
     */
    private void setMainUiStudentCheckedIn(final String studentNumber) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mainActivity.setUiStudentCheckedIn(studentNumber);
            }
        });
    }


    /**
     * Add the given string to the database container view. This must be done on the UI Thread, because
     * we change the UI.
     *
     * @param addedString The string that is added to the database container view
     */
    private void addStringToDbContainer(final String addedString) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                mainActivity.setDbContainerData(addedString);
            }
        });
    }

    /**
     * This function connects app to the database, but if not connected the boolean will return false.
     *
     * @return Returns if connected or not.
     */
    public boolean getConnection() {
        final boolean[] isConnected = {false};
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Class that is needed
                    Class.forName("com.mysql.jdbc.Driver");

                    //Url to the database
                    String url = MessageFormat.format(mainActivity.getString(R.string.database_url), DB_NAME);
                    dbConnection = DriverManager.getConnection(url, DB_USER_NAME, DB_PASSWORD);
                    isConnected[0] = true;
                } catch (SQLException e) {
                    isConnected[0] = false;
                    Log.e("SQL_EXCEPTION", e.getLocalizedMessage());
                } catch (ClassNotFoundException e) {
                    isConnected[0] = false;
                    Log.e("Class_NOT_FOUND", e.getLocalizedMessage());
                }
            }
        };
        thread.start();
        try {
            // Wait till this is done, because we need this information
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isConnected[0];
    }

    public String getStudentNumber(final StringBuilder studentCardSerial) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                // Check if device has internet connection
                if (!getConnection()) {
                    showToast(mainActivity.getString(R.string.not_possible_without_internet));
                    mStudentNumber = "no_internet_connection";
                    return;
                }
                try {
                    Statement statement = dbConnection.createStatement();
                    // Statement to check if the Card serial exists in the database
                    ResultSet result = statement.executeQuery("SELECT `StudentCode` FROM `" + STUDENT_CODE_TABLE_NAME + "` WHERE `Serial` = \"" + studentCardSerial.toString() + "\"");

                    if (result.next()) {
                        mStudentNumber = result.getString("StudentCode");
                    } else {
                        mStudentNumber = "student_number_not_found";
                        addStringToDbContainer(mainActivity.getString(R.string.student_card_not_db));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            // This join is to wait till the thread is done. Otherwise, we do not have mStudentNumber
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mStudentNumber;
    }

    public void checkStudentIn(final String studentNumber) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (!getConnection()) {
                    showToast(mainActivity.getString(R.string.not_possible_without_internet));
                    return;
                }
                try {
                    PreparedStatement statement = dbConnection.prepareStatement(
                            "INSERT INTO " + LOGIN_BU_TABLE_NAME + " (studentnummer, checkIn, checkUit) VALUES('"
                                    + studentNumber + "','" + getCurrentDateString() + "','" + getDateInTwoHours() + "');");
                    int changedRow = statement.executeUpdate();

                    // If changed row is 0 no row is changed
                    if (changedRow != 0) {
                        addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.student_checked_in), studentNumber));
                        setMainUiStudentCheckedIn(studentNumber);
                    } else {
                        addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.error_student_not_checked_in), studentNumber));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.error_student_not_checked_in), studentNumber));
                }
            }
        };
        thread.start();
    }

    public void addNewStudentCardSerial(final String newStudentNumber, final String newStudentCardSerial) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = dbConnection.prepareStatement(
                            "INSERT INTO " + STUDENT_CODE_TABLE_NAME + " (StudentCode, Serial, DatumGemaakt) VALUES('"
                                    + newStudentNumber + "','" + newStudentCardSerial + "','" + getCurrentDateString() + "');");
                    int changedRow = statement.executeUpdate();

                    // If changed row is 0 no row is changed
                    if (changedRow != 0) {
                        addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.student_added_db), newStudentNumber));
                    } else {
                        addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.error_student_not_added_db), newStudentNumber));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    addStringToDbContainer(MessageFormat.format(mainActivity.getString(R.string.error_student_not_added_db), newStudentNumber));
                }
            }
        };
        thread.start();
    }

    public void updateBedrijfspuntenTable() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (!getConnection()) {
                    showToast(mainActivity.getString(R.string.not_possible_without_internet));
                    return;
                }
                try {
                    // Make a temporary table and get the max id from studentnumber and day
                    PreparedStatement makeTempTable = dbConnection.prepareStatement(
                            "CREATE TEMPORARY TABLE tmp_user (" +
                                    "SELECT MAX(id) id " +
                                    "FROM " + LOGIN_BU_TABLE_NAME +
                                    " GROUP BY studentnummer, CAST(checkIn AS DATE))");

                    // Delete all Duplicate of the same day, someone can't enter the symposium twice
                    PreparedStatement deleteDuplicates = dbConnection.prepareStatement(
                            "DELETE FROM " + LOGIN_BU_TABLE_NAME + " WHERE id NOT IN (SELECT id FROM tmp_user);");

                    // Drop the temporary table
                    PreparedStatement dropTempTable = dbConnection.prepareStatement("DROP TABLE tmp_user;");

                    makeTempTable.executeUpdate();
                    deleteDuplicates.executeUpdate();
                    dropTempTable.executeUpdate();

                    // Add new students to Bedrijfspunten
                    PreparedStatement addNewStudentsToBedrijfspunten = dbConnection.prepareStatement(
                            "INSERT INTO " + BEDRIJFSPUNTEN_TABLE_NAME + " (Studentnummer, AantalKeerGeweest, AantalBedrijfsuren, AantalBedrijfspunten) " +
                                    "SELECT DISTINCT studentnummer, 0, 0, 0.0 " +
                                    "FROM " + LOGIN_BU_TABLE_NAME + " " +
                                    "WHERE NOT EXISTS (SELECT Studentnummer FROM " + BEDRIJFSPUNTEN_TABLE_NAME + " " +
                                    "WHERE " + BEDRIJFSPUNTEN_TABLE_NAME + ".Studentnummer = " + LOGIN_BU_TABLE_NAME + ".studentnummer);");


                    // Update the bedrijfspunten looking at the entries in LoginBU which are not added yet, see boolean 'ToegevoegdBedrijfspunten'
                    PreparedStatement updateBedrijfspuntenDB = dbConnection.prepareStatement(
                            "UPDATE `" + BEDRIJFSPUNTEN_TABLE_NAME + "` " +
                                    "INNER JOIN `" + LOGIN_BU_TABLE_NAME + "` ON `" + BEDRIJFSPUNTEN_TABLE_NAME + "`.`Studentnummer` = `" + LOGIN_BU_TABLE_NAME + "`.`studentnummer` " +
                                    "SET `AantalKeerGeweest` = `AantalKeerGeweest` + 1, " +
                                    "`AantalBedrijfsuren` = `AantalBedrijfsuren` + 4, " +
                                    "`ToegevoegdBedrijfspunten` = 1 " +
                                    "WHERE `" + LOGIN_BU_TABLE_NAME + "`.`ToegevoegdBedrijfspunten` = 0;");

                    // AantalBedrijfspunten makes use of aantalBedrijsUren, so this is updated later
                    PreparedStatement updateBedrijfspuntenRow = dbConnection.prepareStatement(
                            "UPDATE `" + BEDRIJFSPUNTEN_TABLE_NAME + "` " +
                                    "SET `AantalBedrijfspunten`= round((`AantalBedrijfsuren` / 28), 1);");

                    // Execute the updates.
                    addNewStudentsToBedrijfspunten.executeUpdate();
                    updateBedrijfspuntenDB.executeUpdate();
                    updateBedrijfspuntenRow.executeUpdate();
                    // Set this message in the db container text view
                    addStringToDbContainer(mainActivity.getString(R.string.updated_table));

                } catch (SQLException e) {
                    e.printStackTrace();
                    addStringToDbContainer(mainActivity.getString(R.string.error_updating_bedrijfspunten_table));
                }
            }
        };
        thread.start();
    }
}
