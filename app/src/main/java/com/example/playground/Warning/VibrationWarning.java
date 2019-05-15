package com.example.playground.Warning;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Date;

public class VibrationWarning extends AsyncWarning {

    private Vibrator haptic;
    private Context context;
    private static boolean terminated;
    private int vibrateTime = 500;
    private static boolean running;

    public VibrationWarning(Context context) {
        haptic = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        this.context = context;
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
            while ((new Date().getTime() - timeBefore) < 1000) ;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    haptic.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    haptic.vibrate(500);
                }
                while ((new Date().getTime() - timeBefore) < 1000) ;
            }
        }
    }

    public void setVibrateTime(int time){
        vibrateTime = time;
    }

    protected void callback() {

    }

    public boolean cancel() {
        running = false;
        return running;
    }
}