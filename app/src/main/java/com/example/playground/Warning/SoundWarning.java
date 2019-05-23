package com.example.playground.Warning;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

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
        if(warning == null){
            warning = new SoundWarning(context);
        }
    }

    public static SoundWarning getWarning() {
        if (warning != null && running){
            Log.d("TEST", "TEST");
            return warning;
        }
        Log.d("TEST2", "TEST2");

        return new SoundWarning(context);
    }

    public void action() {
        if (!running) {
            running = true;
            mp.setLooping(true);
            mp.start();
            while (mp.isLooping() && running) ;
            if (mp.isPlaying()) {
                mp.setLooping(false);
                mp.stop();
            }
            mp.release();
        }
    }

    protected void callback() {
        warning = new SoundWarning(context);
    }

    public boolean cancel() {
        running = false;
        return running;
    }

}