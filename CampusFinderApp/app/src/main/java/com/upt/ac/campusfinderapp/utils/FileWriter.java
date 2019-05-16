package com.upt.ac.campusfinderapp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import io.indoorlocation.core.IndoorLocation;

import static com.firebase.ui.auth.AuthUI.TAG;

public class FileWriter {

    private Context context;

    public FileWriter(Context context){
        this.context = context;
    }

    public void writeToExternalStorage(WifiAccessPoint wifiAccessPoint, IndoorLocation location, double distance){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

        String filename = sdf.format(System.currentTimeMillis()) + ".txt";

        File root = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), "CampusFinder");
        root.mkdir();
        File file = new File(root, filename);

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(sdf.format(System.currentTimeMillis()));
            pw.println("wap " + wifiAccessPoint.getSSID());
            pw.println("level " + wifiAccessPoint.getLevel());
            pw.println("freq " + wifiAccessPoint.getFrequency());
            pw.println("distance " + distance);
            pw.println("latitude " + location.getLatitude());
            pw.println("longitude " + location.getLongitude());
            pw.println("----");
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
