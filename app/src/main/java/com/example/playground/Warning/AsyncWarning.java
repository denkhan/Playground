package com.example.playground.Warning;

import android.os.AsyncTask;

public abstract class AsyncWarning extends AsyncTask<Void, Void, String> {

    private static boolean stopped;

    @Override
    protected String doInBackground(Void... params){
        if (!stopped) {
            action();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        callback();
    }

    public static void stop() {
        stopped = true;
    }

    public static void resume() {
        stopped = false;
    }

    public boolean isStopped() {
        return stopped;
    }

    public abstract void action();
    protected abstract void callback();
    public abstract boolean cancel();
}

