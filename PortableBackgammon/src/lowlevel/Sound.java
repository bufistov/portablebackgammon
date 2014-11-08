/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lowlevel;



import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;
/**
 *
 * @author Gaz
 */
public class Sound // Holds one audio file
    {
 
  public Sound(String filename)
  {
        if (CustomCanvas.SOUND_ON)
        {
            if (clip==null)
                loadSound(filename);
            else
                _("Sound already pre-cached - "+filename);
        }
        else
        {
            _("NOT LOADING SINCE SOUND IS TURNED OFF.");
        }
      
    //loadStartupSound();
    //playStartUpSound();
    
  }

  File file;
   AudioInputStream audioInputStream;
    AudioFormat audioFormat;
    DataLine.Info dataLineInfo;
     Clip clip ;



       // USE THESE TO TEST AUDIO ON WINDOWS.
  public void loadSound(String filename) {
    //Runnable soundPlayer = new Runnable() {
     //  public void run() {
          try {
        //       java.io.File currentDir = new java.io.File("");
      //  System.out.println("path determined "+currentDir.getAbsolutePath());

             // use one of the WAV of Windows installation
              file = new File(filename);//System.getenv("windir") + "/" +"media/tada.wav");
            //  String path=getClass().getResource(filename).getFile();// .toString();//new java.io.File(".").getCanonicalPath()+file;
            //  _("path: ["+path+"]");

//            String path = getClass().getResource("/"+filename).getFile();
         //   path=path.replace("%20", " ");
         //   _("path:   "+path);

            InputStream s = this.getClass().getResourceAsStream(filename);

            //getClass().getResource("/"+filename).getFile()
         //     audioInputStream = AudioSystem.getAudioInputStream(new FileInputStream(path));//"C:/Documents and Settings/Gaz/Desktop/SERIOUS/ALL_CODE_ON_ASPIRE/ALL_NETBEANS_PROJECTS/PortableBackgammon/src/diceroll.wav"     ));
            audioInputStream = AudioSystem.getAudioInputStream(s);
            audioFormat = audioInputStream.getFormat();
             dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
              clip = (Clip) AudioSystem.getLine(dataLineInfo);
           clip.open(audioInputStream);
             ////clip.start();
          } catch (Exception e) {
             e.printStackTrace();
          }
    //   }
    //};
   // Thread soundPlayingThread = new Thread(soundPlayer);
   // soundPlayingThread.start();
 }
  public void playSound()
  {
      if (CustomCanvas.SOUND_ON)
        {
              clip.setFramePosition(0);
      clip.start();
        }
        else
        {
            _("NOT PLAYING SINCE SOUND IS TURNED OFF.");
        }
      /*try{
      clip.open(audioInputStream);
      }catch(Exception e)
      {
          HAL._E("cant play sound "+e);
      }*/
    
  }







public void testSound()
{
_("TEST SOUND CALLED.");
    //WINDOWS ONLY.
   // loadStartupSound();
   // playStartUpSound();
}


     // USE THESE TO TEST AUDIO ON WINDOWS.
  public void loadStartupSound() {
    //Runnable soundPlayer = new Runnable() {
     //  public void run() {
          try {
              File file=null;
   AudioInputStream audioInputStream=null;
    AudioFormat audioFormat=null;
    DataLine.Info dataLineInfo=null;
     Clip clip=null ;
             // use one of the WAV of Windows installation
              file = new File(System.getenv("windir") + "/" +"media/tada.wav");
              audioInputStream = AudioSystem.getAudioInputStream(new FileInputStream(file));
              audioFormat = audioInputStream.getFormat();
             dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
              clip = (Clip) AudioSystem.getLine(dataLineInfo);
             clip.open(audioInputStream);
             ////clip.start();
          } catch (Exception e) {
             e.printStackTrace();
          }
    //   }
    //};
   // Thread soundPlayingThread = new Thread(soundPlayer);
   // soundPlayingThread.start();
 }
  public void playStartUpSound()
  {
      clip.start();
  }

  //load and play in a thread.
  /*private void playStartupSound() {
    Runnable soundPlayer = new Runnable() {
       public void run() {
          try {
             // use one of the WAV of Windows installation
             File tadaSound = new File(System.getenv("windir") + "/" +
                              "media/tada.wav");
             AudioInputStream audioInputStream = AudioSystem
                   .getAudioInputStream(new FileInputStream(tadaSound));
             AudioFormat audioFormat = audioInputStream
                   .getFormat();
             DataLine.Info dataLineInfo = new DataLine.Info(
                   Clip.class, audioFormat);
             Clip clip = (Clip) AudioSystem
                   .getLine(dataLineInfo);
             clip.open(audioInputStream);
             clip.start();
          } catch (Exception e) {
             e.printStackTrace();
          }
       }
    };
    Thread soundPlayingThread = new Thread(soundPlayer);
    soundPlayingThread.start();
 }*/


private void _(String s)
    {
        HAL._("Sound{}:"+s);
    }


    }
