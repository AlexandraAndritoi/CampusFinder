package com.upt.ac.campusfinderapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upt.ac.campusfinderapp.Manifest;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.menu.MenuActivity;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 123;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 456;

    private final String ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private final String ACCESS_COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("logged",false)) {
            goToMainActivity();
        }

        initFirebase();
    }

    private void initFirebase() {
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid == null)
            createSignInIntent();
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers;
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
//                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                saveUserCredentials();
                goToMainActivity();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void saveUserCredentials() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("logged",true).apply();
        sharedPreferences.edit().putString("id",user.getUid()).apply();
        sharedPreferences.edit().putString("username",user.getDisplayName()).apply();
        sharedPreferences.edit().putString("email",user.getEmail()).apply();
    }

    private void goToMainActivity() {
        getLocationPermission();
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivityForResult(intent, RC_SIGN_IN);
        LoginActivity.this.finish();
    }

    private void getLocationPermission() {
        String permissions[] = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this.getApplicationContext(), ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
