package com.example.playground;

import android.location.Location;
import android.util.Log;

import java.io.Serializable;

public class Child implements Serializable {
    private String name;

    private double pLat;
    private double pLon;
    private double cLat;
    private double cLon;

    public Child(String name, Location location){
        this.name = name;
        //this.distance = distance;
        cLat = location.getLatitude();
        cLon = location.getLongitude();
    }

    public double getcLat(){return cLat; }
    public double getcLon(){return cLon; }
    public String getname(){return name; }

    public double distanceBetween(Location l){
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(l.getLatitude() - cLat);
        double lonDistance = Math.toRadians(l.getLongitude() - cLon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(cLat)) * Math.cos(Math.toRadians(l.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}
