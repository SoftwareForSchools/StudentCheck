package hvabucheckin.RFID;

import hvabucheckin.HvABUCheckin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import studentRegistration.UI.RegistrationForm;

public class RFID {
    private OMNIReader reader;
    private javax.swing.Timer readTimer;
    private String lastLogin = "";
    private String serial;
    private final RegistrationForm registrationForm;

    public void setReader(OMNIReader reader) {
        this.reader = reader;
    }
    

    public void openReader() {
        reader.close(); 
        reader.openReader();
        System.out.println(reader.getMessage());
    }

    /*
    *    Functie die pasjes scanned en studenten in- en uitcheckt.
    *    Bij onbekende pas wordt een registratie formulier geopent.
    */
    public void readContinuously() {
        if (readTimer != null) {
            readTimer.stop();
            readTimer = null;
            return;
        }
        int delay = 50;
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                serial = reader.readTag();
                
                if (!serial.equals("")) {  
                    try {
                        if (!lastLogin.equals(serial)) {
                            System.out.println("Serial: " + serial);
                            String student = HvABUCheckin.db.getStudentCode(serial);
                            System.out.println("Student: " + student + " .");
                           
                            if(student.equals("")){
                                //onbekende pas registreren
                                registrationForm.setVisible(true);
                                registrationForm.setSerial(serial);
                                
                                hvabucheckin.HvABUCheckin.client.displayRegisterError();
                                System.out.println("Student staat niet in systeem");
                            }
                            else{
                                //in- en uitchecken
                                hvabucheckin.HvABUCheckin.client.hideRegisterError();
                                HvABUCheckin.db.logTime(student, "", false);
                            }
                        }
                    }
                    catch (Exception ex) {
                        Logger.getLogger(RFID.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else {
                    if (HvABUCheckin.client != null) {
                        HvABUCheckin.client.clearScreen();
                    }
                }
                lastLogin = serial;
            }
        };
        readTimer = new javax.swing.Timer(delay, taskPerformer);
        readTimer.start();
    }
    
    
    public RFID(RegistrationForm regForm){
        this.registrationForm = regForm;
        
        reader = new OMNIReader();
        openReader();
        
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex) {
            Logger.getLogger(RFID.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("\n");
        readContinuously();
    }
}