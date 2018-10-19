package com.hva.symposiumcheckin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public boolean setUserInfo(String dbName, String username, String password){
        try{
            this.dbName = dbName;
            this.username = username;
            this.password = password;
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
