package com.example.playground.Warning;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.playground.R;

public class SoundWarning extends AsyncWarning {

    private MediaPlayer mp;
    private Context context;
    private SoundWarning next;
    private static boolean terminated;

    public SoundWarning(Context context) {
        mp = MediaPlayer.create(context, R.raw.warning);
        this.context = context;
    }

    public void action() {
        while(!terminated) {
            mp.start();
            while (mp.isPlaying() && !terminated) ;
            //mp.stop();
        }
        mp.release();
    }

    protected void callback() {

    }

    public boolean running() {
        return terminated;
    }

    public boolean cancel() {
        if (next != null) {
            terminated = next.cancel(true);
        } else {
            terminated = true;
        }
        return terminated;
    }

}