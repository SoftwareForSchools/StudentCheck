/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin.Database;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class WebsiteReader {
    
    
    private static BufferedReader read(URL url) throws Exception {
        // given a url open a connection
     URLConnection c = url.openConnection();
 
     // set the connection timeout to 5 seconds
     c.setReadTimeout(500);
 
     // use the stream...
        return new BufferedReader(
                new InputStreamReader(
                        c.getInputStream()
                )
        );
    }

    public String[] readStudentFromCardSerial(String cardSerial) throws Exception {
        BufferedReader reader;
       
        try{
            URL url = new URL("http://hva.koen.it/raas/byrfid?rfid=" + cardSerial);
            reader = read(url);    
        } catch(Exception ex){
            String[] error = new String[1];
            error[0] = "DatabaseUnavailable";
            return error;            
        }
        String websiteContent = ""; 
        String line = reader.readLine();
        while (line != null) {
            websiteContent += line;
            line = reader.readLine();
        }

        String[] student = new String[2];
        student[0] = websiteContent.substring(websiteContent.indexOf("\"id\":\"")+6, websiteContent.indexOf("\"", websiteContent.indexOf("\"id\":\"")+7));
        student[1] = websiteContent.substring(websiteContent.indexOf("\"name\":\"")+8, websiteContent.indexOf("\"", websiteContent.indexOf("\"name\":\"")+9));

        return student;
    }
}