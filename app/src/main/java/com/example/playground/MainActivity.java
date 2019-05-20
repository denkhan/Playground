package com.example.playground;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground.Feedback.VibrationFeedback;
import com.example.playground.Filesystem.ChildManager;
import com.example.playground.Warning.AsyncWarning;
import com.example.playground.Adapter.ChildAdapter;
import com.example.playground.Warning.SoundWarning;
import com.example.playground.Warning.VibrationWarning;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener {

    ChildAdapter adapter;
    Parent parent;
    LocationManager locationManager;
    Location myLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    protected Vibrator haptic;
    private boolean warningSoundOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkLocationPermission();
        haptic = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        SoundWarning.setContext(this);
        VibrationWarning.setContext(this);
        VibrationFeedback.setContext(this);
    }

        // Action when clicking on a child
    public void openChild(View v){
        Switch t1 = v.findViewById(R.id.child_active);
        if (t1.isChecked()) {
            Intent intent = new Intent(getBaseContext(), ActivityChild.class);
            TextView t = (TextView) v.findViewById(R.id.child_name);
            intent.putExtra("CHILD", getChild(t.getText().toString()));
            intent.putExtra("PARENT", parent);
            startActivity(intent);
            stopWarnings();
            warning_off();
        }
    }

    public void activateChild(View v) {
        String id = ((TextView)((ConstraintLayout)v.getParent()).findViewById(R.id.child_id)).getText().toString();
        if (((Switch)v).isChecked()) {
            Log.d("AKTIVERA BARN", "HEJ");
            ChildManager.database.get(id).activate(true);
        } else {
            ChildManager.database.get(id).activate(false);
        }
        adapter.notifyDataSetChanged();
    }

    public void createGhostChild(Location location){
        parent = new Parent(location);

        if (ChildManager.registerChild("child1") == 1) {
            Location cLocation = new Location(location);
            cLocation.setLatitude(cLocation.getLatitude()+0.001);
            cLocation.setLongitude(cLocation.getLongitude()+0.001);
            // data to populate the RecyclerView with
            Child alice = ChildManager.database.get("child1");
            alice.setPos(cLocation);
            alice.setAllowedDistance(130);
        }

        if (ChildManager.registerChild("child2") == 1) {
            Location bLocation = new Location(location);
            bLocation.setLatitude(bLocation.getLatitude() - 0.0003);
            bLocation.setLongitude(bLocation.getLongitude() - 0.0003);
            // data to populate the RecyclerView with
            Child bob = ChildManager.database.get("child2");
            bob.setPos(bLocation);
            bob.setAllowedDistance(60);
        }

        if (ChildManager.registerChild("child3") == 1) {
            Location dLocation = new Location(location);
            dLocation.setLatitude(dLocation.getLatitude() - 0.0003);
            dLocation.setLongitude(dLocation.getLongitude() + 0.0003);
            // data to populate the RecyclerView with
            Child charlie = ChildManager.database.get("child3");
            charlie.setPos(dLocation);
            charlie.setAllowedDistance(100);
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_child);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(this, ChildManager.register, location);
        recyclerView.setAdapter(adapter);
    }

    public Child getChild(String name){
        for(Child c : ChildManager.register){
            if(c.getname().equals(name)) return c;
        }
        return null;
    }

        // Action of ADD CHILD button
    public void addChild(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Add child");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_child, null);
        alertDialog.setView(dialogView);

        final EditText eT1 = (EditText) dialogView.findViewById(R.id.edit_text_child_name);
        final EditText eT2 = (EditText) dialogView.findViewById(R.id.edit_text_child_distance);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String m = eT1.getText().toString();
                        //int temp = Integer.parseInt(eT2.getText().toString());
                        Location l = new Location(myLocation);
                        double random = new Random().nextInt(200)-100;
                        l.setLatitude(l.getLatitude()+random/100000);
                        random = new Random().nextInt(200)-100;
                        l.setLongitude(l.getLongitude()-random/100000);
                        int result = ChildManager.registerChild(m);
                        dialog.dismiss();
                        if (result == 1) {
                            Child child = ChildManager.database.get(m);
                            child.setPos(l);
                            child.setAllowedDistance(Integer.parseInt(eT2.getText().toString()));
                            //Children.add(new Child(m, temp));
                            adapter.notifyDataSetChanged();
                            userRegisteredMessage(m);
                        } else if (result == -1) {
                            // user doesn't exist in the database, do something
                            userNotFoundMessage(m);
                        } else {
                            // child already registered
                            userAlreadyRegisteredMessage(m);
                        }
                    }
                });
        alertDialog.show();
    }

    private void vibrate(int time) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haptic.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            haptic.vibrate(time);
        }
    }

    private void userNotFoundMessage(String username) {
        toastMessage("Account for : " + username + " was not found", 15000, Color.RED, Color.WHITE);
        vibrate(600);
    }

    private void userAlreadyRegisteredMessage(String username) {
        toastMessage("Account for : " + username + " is already registered", 15000, Color.GRAY, Color.WHITE);
        for (int i = 0; i < 2; i++) {
            long timeBefore = new Date().getTime();
            vibrate(150);
            while ((new Date().getTime() - timeBefore) < 220) ;
        }
    }

    private void userRegisteredMessage(String username) {
        toastMessage("Account for : " + username + " is registered", 15000, Color.GREEN, Color.WHITE);
        vibrate(100);
    }

    //source: https://stackoverflow.com/questions/31175601/how-can-i-change-default-toast-message-color-and-background-color-in-android
    private void toastMessage(String message, int duration, int background_color, int text_color) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(background_color, PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(text_color);

        toast.show();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    500,
                    1, this);


            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            myLocation = location;
            if(location!=null){
                createGhostChild(location);
            }        }
        resumeWarnings();
        VibrationFeedback.getFeedback().cancel();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("No location")
                        .setMessage("No location")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void warning() {
        if(!warningSoundOn){
            SoundWarning.getWarning().execute();
            VibrationWarning temp = VibrationWarning.getWarning();
            //source: https://stackoverflow.com/questions/15471831/asynctask-not-running-asynchronously
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                temp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                temp.execute();
            }
        }
        warningSoundOn = true;

    }

    public void warning_off() {
        Log.d("HALLÅ", "HALLÅ");
        SoundWarning.getWarning().cancel();
        VibrationWarning.getWarning().cancel();
        warningSoundOn = false;
    }

    public void stopWarnings() {
        AsyncWarning.stop();
    }

    public void resumeWarnings() {
        AsyncWarning.resume();
    }

    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;
        if(parent==null){
            createGhostChild(location);
        }else {
            parent.setLocation(location);
            adapter.setLoc(location);
            adapter.notifyDataSetChanged();
            boolean childFarAway = false;
            for (Child child : ChildManager.register) {
                childFarAway = childFarAway || (!child.inRange(location) && child.isActive());
            }
            if (childFarAway) {
                warning();
            } else {
                warning_off();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) { }

}
