package com.upt.ac.campusfinderapp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import io.indoorlocation.core.IndoorLocation;

import static com.firebase.ui.auth.AuthUI.TAG;

public class FileWriter {

    private Context context;

    public FileWriter(Context context){
        this.context = context;
    }

    public void writeToExternalStorage(WifiAccessPoint wifiAccessPoints[], IndoorLocation location, double distances[]){
        for(int i=0; i<3; i++){
            writeToExternalStorage(wifiAccessPoints[i], location, distances[i]);
        }
    }

    public void writeToExternalStorage(WifiAccessPoint wifiAccessPoint, IndoorLocation location, double distance){
        SimpleDateFormat ddMmYyyy = new SimpleDateFormat("dd MM yyyy");
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
        String filename = ddMmYyyy.format(System.currentTimeMillis()) + ".txt";
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), filename);
        try {
            java.io.FileWriter fileWriter = new java.io.FileWriter(file, true);
            fileWriter.append(format.format(System.currentTimeMillis())+ "\n");
            fileWriter.append("wap " + wifiAccessPoint.getSSID() + "\n");
            fileWriter.append("level " + wifiAccessPoint.getLevel() + "\n");
            fileWriter.append("freq " + wifiAccessPoint.getFrequency() + "\n");
            fileWriter.append("distance " + distance + "\n");
            fileWriter.append("latitude " + location.getLatitude() + "\n");
            fileWriter.append("longitude " + location.getLongitude() + "\n");
            fileWriter.append("----\n");
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
