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
        database.put("child4", new Child("Brad", R.drawable.four));
        database.put("child5", new Child("Bart", R.drawable.five));
        database.put("child6", new Child("Lisa", R.drawable.six));
        database.put("child7", new Child("Romeo", R.drawable.seven));
        database.put("child8", new Child("Charmander", R.drawable.eight));
        database.put("child9", new Child("Pikachu", R.drawable.nine));
        database.put("child10", new Child("Name", R.drawable.ten));
    }
}
