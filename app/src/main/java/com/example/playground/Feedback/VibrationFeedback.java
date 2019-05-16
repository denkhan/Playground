package com.example.playground.Feedback;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.example.playground.Warning.AsyncWarning;
import com.example.playground.Warning.VibrationWarning;

import java.util.Date;

public class VibrationFeedback extends AsyncWarning {

    private static int delay;
    public static boolean running;
    private static VibrationFeedback warning;
    private static Context context;
    private static int vibrateTime;
    private Vibrator haptic;

    private VibrationFeedback(Context context) {
        haptic = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
    }

    public static void setContext(Context context) {
        VibrationFeedback.context = context;
    }

    public static VibrationFeedback getFeedback() {
        if (warning != null){
            return warning;
        }
        return new VibrationFeedback(context);
    }

    @Override
    protected String doInBackground(Void... params){
        action();
        return "";
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
                while ((new Date().getTime() - timeBefore) < vibrateTime + delay && running) ;
                //vibrateTime = tempTime;
            }
        }
    }

    protected void callback() {
        warning = null;
    }

    public static void configureVibration(int time, int delay) {
        VibrationFeedback.vibrateTime = time;
        VibrationFeedback.delay = delay;
    }

    public boolean cancel() {
        running = false;
        return running;
    }
}
