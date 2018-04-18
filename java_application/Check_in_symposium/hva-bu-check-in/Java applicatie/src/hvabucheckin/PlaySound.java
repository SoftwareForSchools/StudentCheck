/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hvabucheckin;

        import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

/**
 *
 * @author DaanZeeuwe
 */
public class PlaySound {
   // Constructor
   public PlaySound(String soundName) {
      try {
         // Open an audio input stream.
         URL url = this.getClass().getClassLoader().getResource(soundName);
         AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         // Get a sound clip resource.
         Clip clip = AudioSystem.getClip();
         // Open audio clip and load samples from the audio input stream.
         clip.open(audioIn);
         clip.start();
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }
   
   
}
