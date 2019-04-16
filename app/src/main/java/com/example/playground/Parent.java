package com.example.playground;

import android.location.Location;

import java.io.Serializable;

public class Parent implements Serializable {

    private double lat;
    private double lon;

    public Parent(Location l){
        lat = l.getLatitude();
        lon = l.getLongitude();
    }

    public double getLat() {return lat;}
    public double getLon() {return lon;}
    public void setLocation(Location l){
        lat = l.getLatitude();
        lon = l.getLongitude();
    }

}
