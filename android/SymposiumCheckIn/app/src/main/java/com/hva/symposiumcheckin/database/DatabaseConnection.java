package com.hva.symposiumcheckin.database;

import android.widget.Toast;

import com.hva.symposiumcheckin.MainActivity;
import com.hva.symposiumcheckin.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;
    private static DatabaseConnection singleton;
    private static boolean isConnected;

    private MainActivity mContext;

    // TODO: Create a 'remember me' button, which remebers user
    private String dbName;
    private String username;
    private String password;

    static {
        singleton = new DatabaseConnection();
    }

    private DatabaseConnection() {
    }

    /**
     * Checks whether there is a database connection or not, and returns a connection.
     * @return A connection to the database.
     */
    public Connection getConnection() {
        isConnected = false;
        if (connection != null) {
            closeConnection();
        }
        if (connection == null) {
            try {
                //TODO: Get a secure connection, at the moment not supported by Oege server.
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://oege.ie.hva.nl:3306/" + dbName
                            + "?allowMultiQueries=true";
                // If oege will support SSL in the future, just uncomment this and it should work.
//                        + "verifyServerCertificate=true"
//                        + "&useSSL=true"
//                        + "&requireSSL=true";

                connection = DriverManager.getConnection(url, username, password);
                isConnected = true;

            } catch (Exception e) {
                e.printStackTrace();
                addStringToDbContainer("Inloggegevens incorrect");
            }
        }
        setUiAfterDatabaseConnection(isConnected);
        return connection;
    }

    /**
     * Gets a single instance of the database, ensures that there can only be óne instance.
     * @return A connection to the database.
     */
    public static DatabaseConnection getInstance() {
        return singleton;
    }

    /**
     * Is called after each statement, so every query has a new connection.
     * Also ensures that the UI (whether database is connected) is updated frequently.
     */
    public void closeConnection() {
        try {
            connection = null;
        } catch (Exception e) {
            addStringToDbContainer(mContext.getString(R.string.failed_closing_sql_connection));
            e.printStackTrace();
        }
    }

    /**
     * Sets the credentials based on user input
     * @param dbName the databasename to be set
     * @param username the database username to be set
     * @param password the database password to be set
     * @return Whether the login credentials were correct.
     */
    public boolean setUserInfo(String dbName, String username, String password) {
        final boolean userStatus[] = {false};
        this.dbName = dbName;
        this.username = username;
        this.password = password;

        Thread thread = new Thread() {
            @Override
            public void run() {
                // Check if is connected to database
                try {
                    getConnection();
                    if (getStatus()) {
                        userStatus[0] = true;
                    } else {
                        userStatus[0] = false;
                        showToast("Incorrecte combinatie van databasenaam, gebruikersnaam en wachtwoord");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            // This join is to wait till the thread is done. Otherwise we have no reference to the actual isConnected.
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userStatus[0];
    }

    /**
     * Adds a string to the database container (Message box).
     * @param addedString The string to be added to the dbContainer.
     */
    private void addStringToDbContainer(final String addedString) {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                mContext.setDbContainerData(addedString);
            }
        });
    }

    /**
     * Changes the UI based on the status of the database.
     * @param isConnected The connection status of the database.
     */
    // Make MainAcitvity execute this by calling the .runOnUiThread function.
    private void setUiAfterDatabaseConnection(final boolean isConnected) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setUiAfterDatabaseConnection(isConnected);
            }
        });
    }

    /**
     * Alerts the user via a toast.
     * @param stringToShow The string to show in the toast.
     */
    private void showToast(final String stringToShow) {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mContext, stringToShow, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Makes it possible to reference the main activity from within this class.
     * @param mainActivity A refference to the mainActivity.
     */
    public void setMainActivity(MainActivity mainActivity) {
        this.mContext = mainActivity;
    }

    /**
     * @return The user's database username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return The user's database password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return The user's database name
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @return A status about the database connection.
     */
    public boolean getStatus() {
        return isConnected;
    }
}
