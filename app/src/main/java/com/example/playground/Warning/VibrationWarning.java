package com.example.playground.Warning;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Date;

public class VibrationWarning extends AsyncWarning {

    protected Vibrator haptic;
    private int vibrateTime = 500;
    private static boolean running;

    public VibrationWarning(Context context) {
        haptic = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
    }

    public void action() {
        // source: https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
        if (!running) {
            running = true;
            while (running) {
                long timeBefore = new Date().getTime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    haptic.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    haptic.vibrate(vibrateTime);
                }
                while ((new Date().getTime() - timeBefore) < 2 * vibrateTime && running) ;
            }
        }
    }

    protected void callback() {

    }

    public boolean cancel() {
        running = false;
        return running;
    }
}