package com.example.playground.Adapter;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.playground.R;

public class SoundWarning extends AsyncWarning {

    private MediaPlayer mp;
    private Context context;
    private SoundWarning next;
    private boolean terminated = false;

    public SoundWarning(Context context) {
        mp = MediaPlayer.create(context, R.raw.warning);
        this.context = context;
    }

    protected void action() {
        while(!terminated) {
            mp.start();
            while (mp.isPlaying() && !terminated) ;
            //mp.stop();
        }
        mp.release();
    }

    protected void callback() {

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