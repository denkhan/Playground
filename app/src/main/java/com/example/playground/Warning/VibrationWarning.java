package com.example.playground.Warning;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Date;

public class VibrationWarning extends AsyncWarning {

    private Vibrator haptic;
    private Context context;
    private VibrationWarning next;
    private static boolean terminated;

    public VibrationWarning(Context context) {
        haptic = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        this.context = context;
    }

    public void action() {
        // source: https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
        while(!terminated) {
            long timeBefore = new Date().getTime();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                haptic.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                haptic.vibrate(500);
            }
            while ((new Date().getTime() - timeBefore) < 1000) ;
        }
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