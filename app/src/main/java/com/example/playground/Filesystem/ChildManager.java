package com.example.playground.Filesystem;

import android.content.Context;
import android.content.ContextWrapper;

import com.example.playground.Child;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChildManager {

    private File directory;
    private ArrayList<String> register;
    private ArrayList<Child> children;

    //source: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android/35827955#35827955
    //source: https://examples.javacodegeeks.com/core-java/io/fileoutputstream/how-to-write-an-object-to-file-in-java/
    public ChildManager(Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        loadRegister();
        loadChildren();
    }

    public boolean saveChild(Child child) {
        String name = child.getname();
        File mypath = new File(directory, name);
        File registerPath = new File(directory, "register");
        boolean saved = false;
        register.add(name);
        if (WriteObjectToFile(register.toArray(), registerPath)) {
            saved = WriteObjectToFile(child, mypath);
            if (!saved) {
                register.remove(name);
                WriteObjectToFile(register.toArray(), registerPath);
            }
        }
        return saved;
    }

    private void loadChildren() {
        ArrayList<Child> children = new ArrayList<>();
        for (String name : register) {
            children.add(loadChild(name));
        }
        this.children = children;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    private void loadRegister() {
        File mypath = new File(directory, "register");
        Object register = ReadObjectFromFile(mypath);
        if (register != null) {
            this.register = new ArrayList<>(Arrays.asList((String[]) register));
        }
        register = new ArrayList<>();
    }

    private Child loadChild(String name) {
        File mypath = new File(directory, name);
        Object child = ReadObjectFromFile(mypath);
        return (child != null) ? (Child) child : null;
    }

    public boolean deleteChild(Child child) {
        boolean deleted = false;
        String name = child.getname();
        File registerPath = new File(directory, "register");
        register.remove(name);
        if (WriteObjectToFile(register.toArray(), registerPath)) {
            deleted = new File(directory, name).delete();
            if (!deleted) {
                register.add(name);
                WriteObjectToFile(register.toArray(), registerPath);
            }
        }
        return deleted;
    }

    private boolean WriteObjectToFile(Object serObj, File path) {

        try {

            FileOutputStream fileOut = new FileOutputStream(path);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Object ReadObjectFromFile(File path) {

        try {

            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
