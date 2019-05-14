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
    static {
        database = new HashMap<>();
        database.put("child1", new Child("Alice", R.drawable.one));
        database.put("child2", new Child("Bob", R.drawable.two));
        database.put("child3", new Child("Charlie", R.drawable.three));
    }
}
