package com.example.playground.Warning;

import android.os.AsyncTask;

public abstract class AsyncWarning extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params){
        action();
        return "";
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        callback();
    }

    protected abstract void action();
    protected abstract void callback();
    public abstract boolean cancel();
    public abstract boolean running();
}

