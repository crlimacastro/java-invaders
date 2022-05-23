package org.newdawn.spaceinvaders.resources;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound implements IResource {
    private Clip clip;

    public Clip getClip() {
        return clip;
    }

    @Override
    public void load(String path) {
        try {
            URL url = this.getClass().getClassLoader().getResource(path);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            this.clip = clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void unLoad() {
        clip.close();
        clip = null;
    }
}
