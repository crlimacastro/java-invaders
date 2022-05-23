package org.newdawn.spaceinvaders;

import javax.sound.sampled.FloatControl;
import org.newdawn.spaceinvaders.resources.Sound;

public class AudioPlayer {
    private float volume = 0.75f;
    private boolean muted = false;

    public float getVolume() {
        return volume;
    }

    // Volume percentage [0, 1]
    public void setVolume(float value) {
        volume = Math.max(0, Math.min(1, value));
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean value) {
        muted = value;
    }

    public void playSound(Sound sound) {
        if (muted) return;
        // Set gain
        ((FloatControl)sound.getClip().getControl(FloatControl.Type.MASTER_GAIN)).setValue(20f * (float)Math.log10(volume));
        sound.getClip().setMicrosecondPosition(0);
        sound.getClip().start();
    }
}
