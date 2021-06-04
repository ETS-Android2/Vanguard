package com.sandile.vanguard.Views.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int LOCATION_REQUEST_CODE = 132;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static double longitude, latitude;

    private FloatingActionButton fab_myLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab_myLocation = findViewById(R.id.main_fab_myLocation);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



    }

    @Override
    public void onClick(View v) {//What happens when user clicks on..
        switch (v.getId()) {
            case R.id.main_fab_myLocation://register
                    showMyLocation();
                break;
        }
    }

    private void showMyLocation() {
        getLastLocation();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            getLastLocation();
        } else {
            askLastLocation();
        }
    }

    public Pair<Double, Double> getLocation()
    {
        return new Pair(longitude, latitude);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task != null) {
                        latitude = task.getResult().getLatitude();
                        longitude = task.getResult().getLongitude();

                        Log.d("LocationManager", "Location lat:" + latitude);
                        Log.d("LocationManager", "location long:" + longitude);
                    }
                    else {
                        new SnackTwo().redSnack(this, "Location not changed! \nLocation can't be found!");
                    }
                });
    }

    private void askLastLocation() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new SnackTwo().blackSnack(this, "Ask for permission");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Nice, permissions are granted
                getLastLocation();
            }else{
                new SnackTwo().redSnack(this, "Permission is not granted!");
            }
        }
    }


}