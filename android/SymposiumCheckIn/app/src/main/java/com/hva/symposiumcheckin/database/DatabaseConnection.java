package com.hva.symposiumcheckin.database;

import android.widget.Toast;

import com.hva.symposiumcheckin.MainActivity;
import com.hva.symposiumcheckin.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {

    private static Connection connection;
    private static DatabaseConnection singleton;
    private static boolean status;

    // TODO: Create a 'remember me' button.
    private String dbName;
    private String username;
    private String password;

    static {
        singleton = new DatabaseConnection();
    }

    private DatabaseConnection() {
    }

    public boolean getStatus(){
        return status;
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://oege.ie.hva.nl:3306/"+ dbName+"?useUnicode=true&useJDBCCompliantTimezoneShift" +
                        "=true&useLegacyDatetimeCode=false&serverTimezone=UTC", username, password);
                status = true;
            } catch (Exception e) {
                status = false;
                e.printStackTrace();
            }
        }
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
            e.printStackTrace();
        }
    }
    public boolean setUserInfo(String dbName, String username, String password, final MainActivity mContext) {
        final boolean userStatus[] = {false};
        this.dbName = dbName;
        this.username = username;
        this.password = password;

        Thread thread = new Thread() {
            @Override
            public void run() {
                // Check if device has internet connection
                try {
                    getConnection();
                    if (getStatus()) {
                        userStatus[0] = true;
                    } else {
                        Toast.makeText(mContext, "Incorrecte combinatie van Databasenaam, gebruikersnaam en wachtwoord.", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
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
        return userStatus[0];
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
