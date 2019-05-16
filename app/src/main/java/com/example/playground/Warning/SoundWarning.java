package com.example.playground.Warning;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.playground.Feedback.VibrationFeedback;
import com.example.playground.R;

import java.nio.file.Watchable;

public class SoundWarning extends AsyncWarning {

    private static MediaPlayer mp;
    public static boolean running;
    private static SoundWarning warning;
    private static Context context;

    private SoundWarning(Context context) {
        mp = MediaPlayer.create(context, R.raw.warning);
    }

    public static void setContext(Context context) {
        SoundWarning.context = context;
    }

    public static SoundWarning getWarning() {
        if (warning != null){
            return warning;
        }
        return new SoundWarning(context);
    }

    public void action() {
        if (!running) {
            running = true;
            while (running) {
                mp.start();
                while (mp.isPlaying() && running) ;
                //mp.stop();
            }
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
    }

    protected void callback() {
        warning = null;
    }

    public boolean cancel() {
        running = false;
        return running;
    }

}