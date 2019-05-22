package com.example.playground.Filesystem;

import android.content.Context;
import android.content.ContextWrapper;

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

    public static HashMap<String, Child> database;
    public static ArrayList<Child> register;
    static {
        database = new HashMap<>();
        register = new ArrayList<>();

        database.put("child1", new Child("Alice", R.drawable.one, "child1"));
        database.put("child2", new Child("Bob", R.drawable.two, "child2"));
        database.put("child3", new Child("Charlie", R.drawable.three, "child3"));
        database.put("child4", new Child("Brad", R.drawable.four, "child4"));
        database.put("child5", new Child("Bart", R.drawable.five, "child5"));
        database.put("child6", new Child("Lisa", R.drawable.six, "child6"));
        database.put("child7", new Child("Romeo", R.drawable.seven, "child7"));
        database.put("child8", new Child("Charmander", R.drawable.eight, "child8"));
        database.put("child9", new Child("Pikachu", R.drawable.nine, "child9"));
        database.put("child10", new Child("Name", R.drawable.ten, "child10"));
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
}
