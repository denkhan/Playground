package com.example.playground.Warning;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.playground.R;

public class SoundWarning extends AsyncWarning {

    private MediaPlayer mp;
    private static boolean running;

    public SoundWarning(Context context) {
        mp = MediaPlayer.create(context, R.raw.warning);
    }

    public void action() {
        if (!running) {
            running = true;
            while (running) {
                mp.start();
                while (mp.isPlaying() && running) ;
                //mp.stop();
            }
            mp.release();
        }
    }

    protected void callback() {

    }

    public boolean cancel() {
        running = false;
        return running;
    }

}