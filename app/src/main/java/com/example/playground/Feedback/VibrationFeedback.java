package com.example.playground.Feedback;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;

import com.example.playground.Warning.VibrationWarning;

import java.util.Date;

public class VibrationFeedback extends VibrationWarning {

    private int vibrateTime = 500;
    private static boolean running;

    public VibrationFeedback(Context context) {
        super(context);
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
                while ((new Date().getTime() - timeBefore) < 2 * vibrateTime && running) ;
            }
        }
    }

    public void setVibrateTime(int time){
        vibrateTime = time;
    }

    public boolean cancel() {
        running = false;
        return running;
    }
}
