package com.example.playground.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.playground.MainActivity;
import com.google.android.gms.location.LocationResult;

//source: https://www.youtube.com/watch?v=4xcrZcnBk60
public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.playground.Service.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals((action))) {
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null) {
                    Location location = result.getLastLocation();
                    Log.d("service", "" + location.getLongitude());
                    try {
                        //MainActivity.getInstance().updateLocation(location);
                    } catch (Exception e) {

                    }
                    Toast.makeText(context, location.getLatitude() + "/" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
