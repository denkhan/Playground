package com.example.playground.Filesystem;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.Location;

import com.example.playground.Child;
import com.example.playground.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChildManager {

    private static HashMap<String, Child> database;
    public static ArrayList<Child> register;
    private static boolean init;
    static {
        database = new HashMap<>();
        register = new ArrayList<>();

        database.put("child1", new Child("Anders", R.drawable.one, "child1"));
        database.put("child2", new Child("Bob", R.drawable.two, "child2"));
        database.put("child3", new Child("Charlie", R.drawable.three, "child3"));
        database.put("child4", new Child("Dennis", R.drawable.four, "child4"));
        database.put("child5", new Child("Emil", R.drawable.five, "child5"));
        database.put("child6", new Child("Felicia", R.drawable.six, "child6"));
        database.put("child7", new Child("Gunilla", R.drawable.seven, "child7"));
        database.put("child8", new Child("Hanna", R.drawable.eight, "child8"));
        database.put("child9", new Child("Ida", R.drawable.nine, "child9"));
        database.put("child10", new Child("Julia", R.drawable.ten, "child10"));
    }

    public static Child getChild(String username) {
        for (Child child : register) {
            if (child.getUsername().equals(username)) {
                return child;
            }
        }
        return null;
    }

    public static int registerChild(String username) {
        if (getChild(username) != null && database.get(username) != null) {
            return 0;
        } else if (database.get(username) != null) {
            register.add(database.get(username));
            return 1;
        }
        return -1;
    }

    public static void removeChild(String username) {
        Child temp = getChild(username);
        if (temp != null) {
            register.remove(temp);
        }
    }

    public static void ghostChildrenInit(Location location) {
        if(!init) {
            if (ChildManager.registerChild("child8") == 1) {
                Location cLocation = new Location(location);
                cLocation.setLatitude(cLocation.getLatitude() + 0.001);
                cLocation.setLongitude(cLocation.getLongitude() + 0.001);
                // data to populate the RecyclerView with
                Child alice = ChildManager.database.get("child8");
                alice.setPos(cLocation);
                alice.setAllowedDistance(130);
            }

            if (ChildManager.registerChild("child2") == 1) {
                Location bLocation = new Location(location);
                bLocation.setLatitude(55.71119);
                bLocation.setLongitude(13.20992);
                // data to populate the RecyclerView with
                Child bob = ChildManager.database.get("child2");
                bob.setPos(bLocation);
                bob.setAllowedDistance(60);
            }
            init = true;
        }
    }
}
