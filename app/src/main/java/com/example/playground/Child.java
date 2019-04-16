package com.example.playground;

import android.location.Location;
import android.util.Log;

import java.io.Serializable;

public class Child implements Serializable {
    private String name;
    private int distance;
    private transient Location location;
    private transient Location parentLocation;

    private double pLat;
    private double pLon;
    private double cLat;
    private double cLon;

    public Child(String name, Location location, Location parentLocation/*, int distance*/){
        this.name = name;
        //this.distance = distance;
        this.location = location;
        this.parentLocation = parentLocation;
        setUp();
    }

    private void setUp(){
        pLat = parentLocation.getLatitude();
        pLon = parentLocation.getLongitude();
        cLat = location.getLatitude();
        cLon = location.getLongitude();
    }


    public double getpLat(){return pLat; }
    public double getpLon(){return pLon; }
    public double getcLat(){return cLat; }
    public double getcLon(){return cLon; }
    public String getname(){return name;}
    public int getDist(){return distance;}
    public Location getChildLocation(){return location;}
    public Location getParentLocation(){return parentLocation;}
    public float distanceBetween(Location l){
        if(location.getLatitude() == l.getLatitude()){
            Log.d("DISTANCE", "HEJ");
        }

        return location.distanceTo(l);
    }

}
