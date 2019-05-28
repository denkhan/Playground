package com.example.playground;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground.Feedback.VibrationFeedback;
import com.example.playground.Filesystem.ChildManager;

public class ActivityChild extends AppCompatActivity implements SensorEventListener, LocationListener {

    ImageView compass_img;
    double mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private TextView distance;
    private Parent parent;
    private Child child;
    boolean vibrateOn = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compass_img = findViewById(R.id.compass_img);

        checkPermission();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                10,
                1, this);

        distance = (TextView)findViewById(R.id.distance);

        Intent intent = getIntent();

        child = (Child) intent.getSerializableExtra("CHILD");
        parent = (Parent) intent.getSerializableExtra("PARENT");

        ((ImageView)findViewById(R.id.child_image)).setImageResource(child.getImage());

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location!=null){
            setDistanceText((int) child.distanceBetween(location), child.getAllowedDistance());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            rMat = lowPass(rMat, rMat, 0.4f);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        } else {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mLastAccelerometer = lowPass(event.values.clone(), mLastAccelerometer, 0.08f);
                mLastAccelerometerSet = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mLastMagnetometer = lowPass(event.values.clone(), mLastMagnetometer, 0.18f);
                mLastMagnetometerSet = true;
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
                //SensorManager.getOrientation(rMat, orientation);
                rMat = lowPass(rMat, rMat, 0.2f);
                mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }
        }

        mAzimuth -= bearing(parent.getLat(), parent.getLon(), child.getcLat(), child.getcLon());

        //Log.d("MAZ", ""+mAzimuth);
        //mAzimuth = Math.round(mAzimuth);

        compass_img.setRotation((float)-mAzimuth);
        mAzimuth = (mAzimuth + 720) % 360;

        // mAzimuth 0 -> 180 right, 180 -> 360 left
        try{
            if (mAzimuth < 345 && mAzimuth > 15) {
                int vibrationTime;
                int delay;
                if (mAzimuth < 25 || mAzimuth > 335)  {
                    VibrationFeedback.configureVibration(50, 350);
                } else if (mAzimuth < 30 || mAzimuth > 330) {
                    VibrationFeedback.configureVibration(50, 300);
                } else if (mAzimuth < 45 || mAzimuth > 315) {
                    VibrationFeedback.configureVibration(50, 200);
                } else {
                    VibrationFeedback.configureVibration(50, 100);
                }
                if (!VibrationFeedback.getFeedback().running) {
                    VibrationFeedback.getFeedback().execute();
                }
            }
            else {
                VibrationFeedback.getFeedback().cancel();
            }
        }catch (Exception e){}

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.child_toolbar, menu);
        getSupportActionBar().setTitle(child.getname() + " (Allowed: " + child.getAllowedDistance() + " m)");
        return true;
    }

    public void childSettings(MenuItem item) {
        final AlertDialog alertDialog = new AlertDialog.Builder(ActivityChild.this).create();
        alertDialog.setTitle("Child Settings");
        final LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.child_settings, null);
        alertDialog.setView(dialogView);

        final EditText eT1 = dialogView.findViewById(R.id.edit_text_child_allowed_distance);
        //final EditText eT2 = (EditText) dialogView.findViewById(R.id.edit_text_child_distance);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Editable input = eT1.getText();
                        if (!input.toString().equals("")){
                            ChildManager.getChild(child.getUsername()).setAllowedDistance(Integer.parseInt(input.toString()));
                            child.setAllowedDistance(Integer.parseInt(input.toString()));
                            getSupportActionBar().setTitle(child.getname() + " (Allowed: " + child.getAllowedDistance() + " m)");
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
        eT1.setHint("Allowed distance: " + child.getAllowedDistance() + " m");
    }

    public void stop() {
        mSensorManager.unregisterListener(this,mAccelerometer);
        mSensorManager.unregisterListener(this,mMagnetometer);
        mSensorManager.unregisterListener(this,mRotationV);
        VibrationFeedback.getFeedback().cancel();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    protected double bearing(double startLat, double startLng, double endLat, double endLng){
        double longitude1 = startLng;
        double longitude2 = endLng;
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    @Override
    public void onLocationChanged(Location location) {
        parent.setLocation(location);
        setDistanceText((int) child.distanceBetween(location), child.getAllowedDistance());
    }

    private void setDistanceText(int currentDistance, int allowedDistance) {
        distance.setText("Distance: " + MainActivity.formatDistance(currentDistance));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    public void voice(View v) {
        toastMessage("Function not implemented", 0, Color.GRAY, Color.WHITE);
    }

    //Source: https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings
    private float[] lowPass( float[] input, float[] output, float alpha ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }
}
