package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tomtom.online.sdk.common.location.LatLng;
import com.upt.ac.campusfinderapp.model.SavedPlace;
import com.upt.ac.campusfinderapp.utils.CurrentUserData;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SavedPlaceRepository {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private Context context;

    public SavedPlaceRepository(Context context) {
        this.context = context;
    }

    public void savePlace(String name, String address, LatLng latLng) {
        SavedPlace place = new SavedPlace(address, latLng.getLatitude(), latLng.getLongitude(), name);
        String path = "/user_savedplaces/" + getUserId();
        String key = mDatabase.child(path).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path + "/" + key, place);
        mDatabase.updateChildren(childUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Place saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "An error occurred while saving", Toast.LENGTH_SHORT).show();
                            Log.e("Update Children ", e.getMessage());
                        }
                    });
    }

    private String getUserId() {
        return CurrentUserData.getInstance(context).getId();
    }
}
