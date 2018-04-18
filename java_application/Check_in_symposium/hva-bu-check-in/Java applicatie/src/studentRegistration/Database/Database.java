package studentRegistration.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Maurice
 */
public class Database {
    private static final Locale NL = new Locale("nl", "NL");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", NL);
    
    private Connection conn;
    private final String db = "";
    private final String url  = "jdbc:mysql://oege.ie.hva.nl/";
    private final String user = "";
    private final String pass = "";
    
    public Database(){
        //Create connection
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.conn = DriverManager.getConnection(this.url+this.db, this.user, this.pass);
        }
        catch (Exception e) {
            System.err.println("DB Connection error:\n" + e.toString());
        }
    }
    
    public void execute(String sqlString){
        try{
            PreparedStatement query = this.conn.prepareStatement(sqlString);
            query.executeUpdate();
        }
        catch(Exception e){
            System.err.println("DB error:\n" + e.toString());
        }
    }
    
    public void addStudent(String studentCode, String serial){
        String sqlString = "INSERT INTO `StudentCode` ("
                + "`studentCode`, `serial`, `datumgemaakt`"
                + ") VALUES ("
                + studentCode + ", '"+ serial +"', '"+ DATE_FORMAT.format(new Date()) +"');";
        this.execute(sqlString);
    }
}
