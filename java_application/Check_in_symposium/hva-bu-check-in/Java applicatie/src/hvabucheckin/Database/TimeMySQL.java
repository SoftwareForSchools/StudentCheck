/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin.Database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author itopia
 */
public class TimeMySQL {
    private static final Locale NL = new Locale("nl", "NL");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", NL);
    
    private Connection conn = null;
    private static final String dbUser = "";
    private static final String dbPassword = "";
    private final String table = "";
    private String url;
   
    /**
     * This will make connection with the database
     *
     * @return
     */
    public Connection getConnection() {
        // Loading driver
        try {
            url = "jdbc:mysql://oege.ie.hva.nl/" + this.dbUser;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException cnfex) {
            cnfex.printStackTrace();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (Exception excp) {
            excp.printStackTrace();
        }
        return conn;
    }

    public ArrayList<TimeRecord> getDatabase() {
        Statement stmt;
        ResultSet rs;
        PreparedStatement preparedStatement;
        conn = getConnection();

        ArrayList<TimeRecord> timeRecords = new ArrayList<>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM "+this.table+" WHERE DATE(checkIn) = CURDATE();");
            while (rs.next()) {
                System.out.println(rs.getString("studentnummer"));
                String studentnummer = rs.getString("studentnummer");
                Timestamp checkIn = rs.getTimestamp("checkIn");
                Timestamp checkUit = rs.getTimestamp("checkUit");
                timeRecords.add(new TimeRecord(studentnummer, checkIn, checkUit/*, duratie, nietUitgecheckt*/));
                
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            return timeRecords;
        }
    }
    
    public void createRecord(TimeRecord record){
        PreparedStatement statement;
        conn = getConnection();
        try {
            Date dateNow = new Date();
            Date endDate = new Date(dateNow.getTime() + 2 * (3600 * 1000));
            System.out.println("CREATE RECORD2: INSERT INTO "+this.table+" (studentnummer, checkIn, checkUit) VALUES('"
                    + record.studentnummer + "','" + DATE_FORMAT.format(dateNow) +"'"+ "','" + DATE_FORMAT.format(endDate) +"');");
            System.out.println(record.toString());
            statement = conn.prepareStatement(
                    "INSERT INTO "+this.table+" (studentnummer, checkIn, checkUit) VALUES('"
                    + record.studentnummer + "','" + DATE_FORMAT.format(dateNow) + "', '"+ DATE_FORMAT.format(endDate) + ");");
            statement.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void finishRecord(String studentnummer, Timestamp checkIn, Timestamp checkUit/*, String duratie*/) {
        Statement stmt;
        ResultSet rs;
        PreparedStatement preparedStatement;
        conn = getConnection();
        try {
            System.out.println("FINISH RECORD: UPDATE "+ this.table
                    + " SET checkUit='" + checkUit + "' "
                    + "WHERE studentnummer='" + studentnummer + "' AND checkIn='" + checkIn + "';");
            preparedStatement = conn.prepareStatement(
                    "UPDATE " + this.table
                    + " SET checkUit='" + checkUit + "'"
                    + "WHERE studentnummer='" + studentnummer + "' AND checkIn='" + checkIn + "';");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getStudentCode(String serial){
        Statement stmt;
        ResultSet rs;
        PreparedStatement preparedStatement;
        conn = getConnection();
        String studentnummer = "";
        
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT `StudentCode` FROM `StudentCode` WHERE `Serial` = \""+serial+"\"");
            while (rs.next()){
                studentnummer = rs.getString("StudentCode");                
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return studentnummer;
    }
}
