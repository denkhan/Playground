package com.example.playground;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.TextView;

import com.example.playground.Warning.AsyncWarning;
import com.example.playground.Adapter.ChildAdapter;
import com.example.playground.Warning.SoundWarning;
import com.example.playground.Warning.VibrationWarning;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LocationListener {

    ChildAdapter adapter;
    Parent parent;
    ArrayList<Child> Children = new ArrayList<>();
    private AsyncWarning[] warnings = new AsyncWarning[2];
    LocationManager locationManager;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermission();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                500,
                1, this);


        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        myLocation = location;
        if(location!=null){
            createGhostChild(location);
        }

    }
        // Action when clicking on a child
    public void openChild(View v){
        Intent intent = new Intent(getBaseContext(), ActivityChild.class);
        TextView t = (TextView)v.findViewById(R.id.child_name);
        intent.putExtra("CHILD", getChild(t.getText().toString()));
        intent.putExtra("PARENT", parent);
        startActivity(intent);
    }

    public void createGhostChild(Location location){
        parent = new Parent(location);

        Location cLocation = new Location(location);
        cLocation.setLatitude(cLocation.getLatitude()+0.001);
        cLocation.setLongitude(cLocation.getLongitude()+0.001);
        // data to populate the RecyclerView with
        Children.add(new Child("Alice",cLocation));

        Location bLocation = new Location(location);
        bLocation.setLatitude(bLocation.getLatitude()-0.0003);
        bLocation.setLongitude(bLocation.getLongitude()-0.0003);
        // data to populate the RecyclerView with
        Children.add(new Child("Bob",bLocation));

        Location dLocation = new Location(location);
        dLocation.setLatitude(dLocation.getLatitude()-0.0003);
        dLocation.setLongitude(dLocation.getLongitude()+0.0003);
        // data to populate the RecyclerView with
        Children.add(new Child("Charlie",dLocation));



        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_child);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChildAdapter(this, Children, location);
        recyclerView.setAdapter(adapter);
    }

    public Child getChild(String name){
        for(Child c : Children){
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
                        Children.add(new Child(m,l));
                        //Children.add(new Child(m, temp));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
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

    public void warning(View p) {
        warnings[0] = new SoundWarning(this);
        warnings[1] = new VibrationWarning(this);
        warnings[0].execute();
        //source: https://stackoverflow.com/questions/15471831/asynctask-not-running-asynchronously
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            warnings[1].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            warnings[1].execute();
        }
    }
    public void warning_off(View p) {
        //warnings[0].cancel();
        warnings[0].cancel();
        warnings[1].cancel();
        /*for (AsyncWarning w : warnings) {
            w.terminate();
        }*/
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
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }

}
