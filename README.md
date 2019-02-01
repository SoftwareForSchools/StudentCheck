# StudentCheck #
This project is for scanning if students are present in courses. The project has two consist of two applications, a Java Application and a Android Application.

For more info check the wiki:
https://github.com/obin1000/StudentCheck/wiki

### Database: ###
* Make your own phpmyadmin database
* For example tables, run the sql code in your database. The SQL code is in the following directory: example_database_tables
* Tip: Make regular updates of your database, so when something goes wrong you still have your latest database.

### Android applicatie: ###
* The Android device needs to have a NFC scanner!
* Install Android Studio.
* Open the directory android\SymposiumCheckIn .
* Fill in your database information, in the DatabaseHelper class.
* Pair your phone to your computer (You have to turn on Developer options and Debug modus).
* Build the application on your Android device and you can scan student cards.
	
### Java application: ###
* Install Netbeans.
* Open the following directroy in Netbeans: java_application\Check_in_symposium\hva-bu-check-in\Java applicatie
* Netbeans will tell you there are a few unresolved issues, you can resolve this issues with the mail.jar in java_application\Check_in_symposium\hva-bu-check-in\Java applicatie\libs\javamail-1.4.7 and opencsv-3.0.jar in java_application\Check_in_symposium\hva-bu-check-in\Java applicatie\libs
* Change the database settings in the TimeMySQL.java from the hvabucheckin project and the Database.java file in the studentRegistration project to your own database settings.
* For this application you need a RFID scanner so you need to install the driver for this on your computer, there is an installer for windows in this directory: java_application\Check_in_symposium\Omnium\hid_omnikey_ccid_driver_v2.0.0.4 .
