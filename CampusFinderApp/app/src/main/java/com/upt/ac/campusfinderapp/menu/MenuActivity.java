package com.upt.ac.campusfinderapp.menu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.navisens.NavisensIndoorLocationProvider;
import com.upt.ac.campusfinderapp.savedplaces.OnFragmentInteractionListener;
import com.upt.ac.campusfinderapp.savedplaces.SavedPlacesFragment;
import com.upt.ac.campusfinderapp.settings.SettingsActivity;
import com.upt.ac.campusfinderapp.indoormap.IndoorMapFragment;
import com.upt.ac.campusfinderapp.outdoormap.OutdoorMapFragment;
import com.upt.ac.campusfinderapp.utils.CampusFinderApplication;
import com.upt.ac.campusfinderapp.utils.CurrentUserData;
import com.upt.ac.campusfinderapp.utils.LoginActivity;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.manual.ManualIndoorLocationProvider;
import io.mapwize.mapwizecomponents.ui.MapwizeFragment;
import io.mapwize.mapwizeformapbox.api.LatLngFloor;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener, MapwizeFragment.OnFragmentInteractionListener {

    private Fragment fragment;

    private MapwizePlugin mapwizePlugin;
    private ManualIndoorLocationProvider manualIndoorLocationProvider;
    private NavisensIndoorLocationProvider navisensIndoorLocationProvider;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setOutdoorMapFragmentAsDefault();

        showUserDetails();
    }

    private void setOutdoorMapFragmentAsDefault() {
        fragment = new OutdoorMapFragment();
        startFragment();
    }

    private void showUserDetails() {
        CurrentUserData currentUserData = CurrentUserData.getInstance(getApplicationContext());

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        TextView userName = header.findViewById(R.id.userNameTextView);
        userName.setText(currentUserData.getUsername());

        TextView userEmail = header.findViewById(R.id.userEmailTextView);
        userEmail.setText(currentUserData.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_outdoor_map) {
            fragment = new OutdoorMapFragment();
        } else if (id == R.id.nav_indoor_map) {
            fragment = new IndoorMapFragment();
        } else if (id == R.id.nav_saved_places) {
            fragment = new SavedPlacesFragment();
        } else if (id == R.id.nav_manage) {
            startActivity(SettingsActivity.class);
        } else if (id == R.id.nav_log_out) {
            logOut();
        }

        if(fragment != null) {
            startFragment();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_SHORT).show();
                        setCurrentUserLoggingStateToFalse();
                        startActivity(LoginActivity.class);
                    }
                });
    }

    private void setCurrentUserLoggingStateToFalse() {
        CurrentUserData.getInstance(getApplicationContext()).setLoggingState(false);
    }

    public void startActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void startFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.replace(R.id.screen_area, fragment);

        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO
    }

    @Override
    public void onMenuButtonClick() {

    }

    @Override
    public void onInformationButtonClick(MapwizeObject mapwizeObject) {

    }

    @Override
    public void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin) {
        this.mapwizePlugin = mapwizePlugin;
        requestLocationPermission();
        this.mapwizePlugin.addOnLongClickListener(clickEvent -> {
            LatLngFloor latLngFloor = clickEvent.getLatLngFloor();
            IndoorLocation indoorLocation = new IndoorLocation(manualIndoorLocationProvider.getName(), latLngFloor.getLatitude(), latLngFloor.getLongitude(), latLngFloor.getFloor(), System.currentTimeMillis());
            manualIndoorLocationProvider.dispatchIndoorLocationChange(indoorLocation);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationProvider();
                }
            }
        }
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocationProvider();
        }
    }

    private void setupLocationProvider() {
//        IndoorLocation manualIndoorLocation = new IndoorLocation("Manual", 45.747338, 21.226126, (double)4, System.currentTimeMillis());
        manualIndoorLocationProvider = new ManualIndoorLocationProvider();
//        manualIndoorLocationProvider.setIndoorLocation(manualIndoorLocation);
        navisensIndoorLocationProvider = new NavisensIndoorLocationProvider(this, CampusFinderApplication.NAVISENS_API_KEY, manualIndoorLocationProvider);
        mapwizePlugin.setLocationProvider(navisensIndoorLocationProvider);
    }

    @Override
    public void onFollowUserButtonClickWithoutLocation() {

    }
}
