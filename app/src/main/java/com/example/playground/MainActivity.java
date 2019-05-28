package com.example.playground;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener {

    ChildAdapter adapter;
    Parent parent;
    LocationManager locationManager;
    Location myLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    protected Vibrator haptic;
    private static MainActivity instance;

    private static boolean warningOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SoundWarning.setContext(this);
        VibrationWarning.setContext(this);
        VibrationFeedback.setContext(this);
        checkLocationPermission();

        haptic = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        instance = this;

        //source: http://www.androidtutorialshub.com/android-recyclerview-click-listener-tutorial/
        RecyclerView recyclerView = findViewById(R.id.rv_child);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                removeChild(ChildManager.register.get(position).getUsername());
                vibrate(200);
            }
        }));
    }

    public void removeChild(final String username){
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Remove child");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Remove",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ChildManager.removeChild(username);
                        adapter.notifyDataSetChanged();
                        emptyListFeedback();
                        childChecker(myLocation);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
            }
        });
        alertDialog.show();
    }

        // Action when clicking on a child
    public void openChild(View v){
        Switch t1 = ((View )v.getParent()).findViewById(R.id.child_active);
        if (t1.isChecked()) {
            Intent intent = new Intent(getBaseContext(), ActivityChild.class);
            TextView t = (TextView) v.findViewById(R.id.child_id);
            intent.putExtra("CHILD", ChildManager.getChild(t.getText().toString()));
            intent.putExtra("PARENT", parent);
            startActivity(intent);
            stopWarnings();
            warning_off();
        } else {
            vibrate(100);
        }
    }

    public void activateChild(View v) {
        String id = ((TextView)((ConstraintLayout)v.getParent()).findViewById(R.id.child_id)).getText().toString();
        if (((Switch)v).isChecked()) {
            ChildManager.getChild(id).activate(true);
        } else {
            ChildManager.getChild(id).activate(false);
        }
        childChecker(myLocation);
        adapter.notifyDataSetChanged();
    }

    private void populateGhostChildren(Location location) {
        parent = new Parent(location);

        ChildManager.ghostChildrenInit(location);

        emptyListFeedback();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_child);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(this, ChildManager.register, location);
        recyclerView.setAdapter(adapter);
        childChecker(location);
    }

    private void emptyListFeedback() {
        TextView feedback = findViewById(R.id.empty_list_message);
        if(ChildManager.register.size() < 1) {
            feedback.setVisibility(View.VISIBLE);
        } else {
            feedback.setVisibility(View.INVISIBLE);
        }
    }

        // Action of ADD CHILD button
    public void addChild(View v){
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Add child");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_child, null);
        alertDialog.setView(dialogView);

        final EditText eT1 = (EditText) dialogView.findViewById(R.id.edit_text_child_allowed_distance);
        final EditText eT2 = (EditText) dialogView.findViewById(R.id.edit_text_child_distance);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                public void onClick (DialogInterface dialog,int which){
                    if(!eT1.getText().toString().equals("") && !eT2.getText().toString().equals("")) {
                        String m = eT1.getText().toString();
                        //int temp = Integer.parseInt(eT2.getText().toString());
                        Location l = new Location(myLocation);
                        double random = new Random().nextInt(200) - 100;
                        l.setLatitude(l.getLatitude() + random / 100000);
                        random = new Random().nextInt(200) - 100;
                        l.setLongitude(l.getLongitude() - random / 100000);
                        int result = ChildManager.registerChild(m);
                        dialog.dismiss();
                        if (result == 1 && !m.equals("child2")) {
                            Child child = ChildManager.getChild(m);
                            child.setPos(l);
                            child.setAllowedDistance(Integer.parseInt(eT2.getText().toString()));
                            //Children.add(new Child(m, temp));
                            adapter.notifyDataSetChanged();
                            emptyListFeedback();
                            userRegisteredMessage(m);
                            childChecker(myLocation);
                        } else if (result == 1) {
                            Location bLocation = new Location(myLocation);
                            bLocation.setLatitude(55.71119);
                            bLocation.setLongitude(13.20992);
                            // data to populate the RecyclerView with
                            Child bob = ChildManager.getChild("child2");
                            bob.setPos(bLocation);
                            bob.setAllowedDistance(60);
                            childChecker(myLocation);
                        }else if (result == -1) {
                            // user doesn't exist in the database, do something
                            userNotFoundMessage(m);
                        } else {
                            // child already registered
                            userAlreadyRegisteredMessage(m);
                        }
                    } else {
                        leftOutFieldMessage();
                    }
                }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.GREEN);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
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

    private void leftOutFieldMessage() {
        toastMessage("Failed to add user. Please fill all fields.", Color.RED, Color.WHITE);
        vibrate(400);
    }

    private void userNotFoundMessage(String username) {
        toastMessage("Account for : " + username + " was not found.", Color.RED, Color.WHITE);
        vibrate(600);
    }

    private void userAlreadyRegisteredMessage(String username) {
        toastMessage("Account for : " + username + " is already registered.", Color.GRAY, Color.WHITE);
        for (int i = 0; i < 2; i++) {
            long timeBefore = new Date().getTime();
            vibrate(150);
            while ((new Date().getTime() - timeBefore) < 220) ;
        }
    }

    private void userRegisteredMessage(String username) {
        toastMessage("Account for : " + username + " is registered.", Color.GREEN, Color.WHITE);
        vibrate(100);
    }

    //source: https://stackoverflow.com/questions/31175601/how-can-i-change-default-toast-message-color-and-background-color-in-android
    private void toastMessage(String message, int background_color, int text_color) {
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
        VibrationFeedback.getFeedback().cancel();
        resumeWarnings();
        if (checkLocationPermission()) {

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    40,
                    1, this);


            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            myLocation = location;
            if(location!=null){
                populateGhostChildren(location);
            }

            childChecker(location);
        }
    }

    public boolean checkLocationPermission() {
        AlertDialog temp = null;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    temp = new AlertDialog.Builder(this)
                            .setTitle("Playground")
                            .setMessage("This app needs access to your location to function. Your information will not be shared with third parties.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    /*ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);*/
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                                }
                            })
                            .create();

                    temp.show();

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
        if (!SoundWarning.getWarning().running) {
            SoundWarning.getWarning().execute();
        }

        if (!VibrationWarning.getWarning().running) {
            VibrationWarning temp = VibrationWarning.getWarning();
            //source: https://stackoverflow.com/questions/15471831/asynctask-not-running-asynchronously
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                temp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                temp.execute();
            }
        }
    }

    public void warning_off() {
        Log.d("HALLÅ", "HALLÅ");
        SoundWarning.getWarning().cancel();
        VibrationWarning.getWarning().cancel();
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
            populateGhostChildren(location);
        }else {
            parent.setLocation(location);
            adapter.setLoc(location);
            adapter.notifyDataSetChanged();
            childChecker(location);
        }
    }

    private void childChecker(Location location){
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

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) { }

    public void logOut(MenuItem v) {
        toastMessage("Function not implemented", Color.GRAY, Color.WHITE);
    }

    public void voice(View v) {
        toastMessage("Function not implemented", Color.GRAY, Color.WHITE);
    }

    public static String formatDistance(double distance) {
        double kilometers = distance/1000;
        if (kilometers >= 100) {
            return "99+ km";
        } else if (kilometers >= 10){
            return Math.round(kilometers) + " km";
        } else if (kilometers >= 1) {
            return Math.round(kilometers * 10.0) / 10.0 + " km";
        }
        return Math.round(distance) + " m";
    }

}
