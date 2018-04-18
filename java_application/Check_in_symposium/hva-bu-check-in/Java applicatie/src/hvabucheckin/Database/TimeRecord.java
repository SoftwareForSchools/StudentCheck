/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin.Database;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author DaanZeeuwe
 */
public class TimeRecord {

    String studentnummer; 
    Timestamp checkIn;
    Timestamp checkUit = null;
    
    DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TimeRecord(){
        this.studentnummer = null;
        this.checkIn = null;
        this.checkUit =null;
    }
    
    public TimeRecord(String studentnummer) {
        java.util.Date date= new java.util.Date();
        this.studentnummer = studentnummer;
        this.checkIn = new Timestamp(date.getTime());
        this.checkUit = null;
    }
    
    public TimeRecord(String studentnummer, Timestamp checkIn, Timestamp checkUit/*, String duratie, String nietUitgecheckt*/) {
        this.studentnummer = studentnummer;
        this.checkIn = checkIn;
        this.checkUit = checkUit;
    }
    
    public void finishRecord(){
        Date now = Calendar.getInstance().getTime();
        java.util.Date date= new java.util.Date();
        
        this.checkUit = new Timestamp(date.getTime());
        System.out.println("CHeckLogUit " + this.checkUit);
    }

    public Timestamp getCheckIn() {
        return checkIn;
    }

    public Timestamp getCheckUit() {
        return checkUit;
    }

    public String getStudentnummer() {
        return studentnummer;
    }
    
    public boolean isToday(){
        Calendar cToday = Calendar.getInstance();
        Date today = new Date();
        cToday.setTime(today);
        Calendar cStamp = Calendar.getInstance();
        Date stamp = new Date(checkIn.getTime());
        cStamp.setTime(stamp);
        return(cToday.get(Calendar.YEAR) == cStamp.get(Calendar.YEAR) &&
                cToday.get(Calendar.DAY_OF_YEAR) == cStamp.get(Calendar.DAY_OF_YEAR));        
    }

    @Override
    public String toString() {
        String message;
        message = "studentnummer: " + studentnummer + ";";
        message += " checkIn: " + checkIn + ";";
        message += " checkUit: " + checkUit + ";";
        return message; //To change body of generated methods, choose Tools | Templates.
    }
}
