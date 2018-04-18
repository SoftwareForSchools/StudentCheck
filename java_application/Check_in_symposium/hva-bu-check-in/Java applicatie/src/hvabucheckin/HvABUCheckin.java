/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin;

import hvabucheckin.Database.Database;
import hvabucheckin.GUI.ClientUI;
import hvabucheckin.RFID.RFID;
import studentRegistration.UI.RegistrationForm;
import java.io.IOException;
import javax.swing.UIManager;

/**
 *
 * @author DaanZeeuwe
 * Verbeterd en uitgebreid door Maurice Keetman en Chris Eijgenstein
 */

public class HvABUCheckin {
    public static Database db;
    public static RFID rfid;
    public static ClientUI client;
    public final static String registrationBackup = "";

    public static void main(String[] args) throws IOException{
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        //Setup Database
        db = new Database();
        db.initializeDatabase();
        
        //Setup registratie formulier en start programma
        RegistrationForm registrationForm = new RegistrationForm();
        registrationForm.setFile(registrationBackup);
        rfid = new RFID(registrationForm);
        
        //Open GUI
        client = new ClientUI();
        client.setVisible(true);
        
        while (true) {
            client.updateTime();
        }
    }
}
