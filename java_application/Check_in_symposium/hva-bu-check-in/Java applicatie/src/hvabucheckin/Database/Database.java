/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin.Database;

import hvabucheckin.HvABUCheckin;
import java.util.ArrayList;

/**
 *
 * @author DaanZeeuwe
 */
public class Database {

    public TimeMySQL timeMySQL;
    public static ArrayList<TimeRecord> timeRecords;

    public Database() {
        timeMySQL = new TimeMySQL();
        timeRecords = new ArrayList<>();
    }

    public void initializeDatabase() {
        System.out.println("INIT DATABASE");
        timeRecords.clear();
        timeRecords.addAll(timeMySQL.getDatabase());
    }

    public String getStudentCode(String serial) {
        return timeMySQL.getStudentCode(serial);
    }

    public void logTime(String studentNumber, String name, boolean offline) {
        if (timeRecords.size() > 0) {
            System.out.println("SIZE " + timeRecords.size());
            for (int i = timeRecords.size() - 1; i >= 0; i--) {
                TimeRecord record = timeRecords.get(i);
                System.out.println("Record " + record.studentnummer + " " + studentNumber);
                if (record.studentnummer.equals(studentNumber)) {

                    if (record.checkUit == null && record.isToday()) {
                        System.out.println("IF " + record.studentnummer + " " + studentNumber);
                        System.out.println(record.toString());
                        logOut(record);
                        break;
                    } else {
                        System.out.println("ELSE " + record.studentnummer + " " + studentNumber);
                        System.out.println("Record equals studentNumber");
                        logIn(studentNumber);
                        break;
                    }
                }
                if (i == 0 && !record.studentnummer.equals(studentNumber)) {
                    System.out.println("i==0");
                    logIn(studentNumber);
                    
                }
            }
        } else {
            System.out.println("DATABASE.java else");
            logIn(studentNumber);
        }
    }

    public void logOut(TimeRecord record) {
        record.finishRecord();
        HvABUCheckin.client.logOut(record.studentnummer, "");
        timeMySQL.finishRecord(record.studentnummer, record.checkIn, record.checkUit);
    }

    public void logIn(String StudentNumber) {
        logIn(new TimeRecord(StudentNumber));
    }

    public void logIn(TimeRecord record) {
        HvABUCheckin.client.logIn(record.studentnummer);
        TimeRecord newRecord = new TimeRecord(record.studentnummer);
        timeRecords.add(newRecord);
        timeMySQL.createRecord(newRecord);
        initializeDatabase();
        System.out.println("logged to timerecords and mysql 2");
    }
}
