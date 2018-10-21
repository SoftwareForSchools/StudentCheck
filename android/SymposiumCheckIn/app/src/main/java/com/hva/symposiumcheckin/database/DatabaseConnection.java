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

    public boolean getStatus() {
        return isConnected;
    }

    public Connection getConnection() {
        isConnected = false;
        if (connection != null) {
            closeConnection();
        }
        if (connection == null) {
            try {
                //TODO: Get a secure connection, at the moment not supported by Oege server.
                Class.forName("com.mysql.jdbc.Driver");
                String url = "jdbc:mysql://oege.ie.hva.nl:3306/" + dbName;
//                        + "?verifyServerCertificate=true"
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

    public static DatabaseConnection getInstance() {
        return singleton;
    }

    public void closeConnection() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            addStringToDbContainer(mContext.getString(R.string.failed_closing_sql_connection));
            e.printStackTrace();
        }
    }

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

    private void addStringToDbContainer(final String addedString) {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                mContext.setDbContainerData(addedString);
            }
        });
    }

    // Make MainAcitvity execute this by calling the .runOnUiThread function.
    private void setUiAfterDatabaseConnection(final boolean isConnected) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setUiAfterDatabaseConnection(isConnected);
            }
        });
    }

    private void showToast(final String stringToShow) {
        mContext.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mContext, stringToShow, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mContext = mainActivity;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }
}
