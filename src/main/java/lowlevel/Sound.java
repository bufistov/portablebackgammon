package lowlevel;

import java.io.InputStream;
import javax.sound.sampled.*;

public class Sound {

    private Clip clip;
    public Sound(String filename, boolean soundOn) {
        if (soundOn) {
            loadSound(filename);
        } else {
            if (clip != null)
                clip.close();
            clip = null;
        }
    }

    public void playSound() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    private void loadSound(String filename) {
        try {
            InputStream stream = this.getClass().getResourceAsStream(filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(stream);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat);
            clip = (Clip) AudioSystem.getLine(dataLineInfo);
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
