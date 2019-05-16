package com.example.playground.Warning;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.playground.Feedback.VibrationFeedback;

import java.util.Date;

public class VibrationWarning extends AsyncWarning {

    private Vibrator haptic;
    protected int vibrateTime = 500;
    public static boolean running;
    private static VibrationWarning warning;
    private static Context context;

    private VibrationWarning(Context context) {
        haptic = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
    }

    public static void setContext(Context context) {
        VibrationWarning.context = context;
    }

    public static VibrationWarning getWarning() {
        if (warning != null){
            return warning;
        }
        return new VibrationWarning(context);
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
        warning = null;
    }

    public boolean cancel() {
        running = false;
        return running;
    }
}