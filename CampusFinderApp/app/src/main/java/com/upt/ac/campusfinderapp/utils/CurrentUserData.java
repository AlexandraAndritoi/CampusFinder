package com.upt.ac.campusfinderapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrentUserData {
    private static CurrentUserData currentUserData;
    private SharedPreferences sharedPreference;

    private final String LOGGED = "logged";
    private final String ID = "id";
    private final String USERNAME = "username";
    private final String EMAIL = "email";

    private CurrentUserData(Context context) {
        sharedPreference = context.getSharedPreferences(CurrentUserData.class.getName(), Context.MODE_PRIVATE);
    }

    public static CurrentUserData getInstance(Context context) {
        if(currentUserData == null) {
            currentUserData = new CurrentUserData(context);
        }
        return currentUserData;
    }

    public void setLoggingState(Boolean bool) {
        sharedPreference.edit().putBoolean(LOGGED, bool).apply();
    }

    public Boolean getLoggingState() {
        return sharedPreference.getBoolean(LOGGED, false);
    }

    public void setId(String id) {
        sharedPreference.edit().putString(ID, id).apply();
    }

    public String getId() {
        return sharedPreference.getString(ID, "");
    }

    public void setUsername(String username) {
        sharedPreference.edit().putString(USERNAME, username).apply();
    }

    public  String getUsername() {
        return sharedPreference.getString(USERNAME, "");
    }

    public void setEmail(String email) {
        sharedPreference.edit().putString(EMAIL, email);
    }

    public String getEmail() {
        return sharedPreference.getString(EMAIL, "");
    }
}
