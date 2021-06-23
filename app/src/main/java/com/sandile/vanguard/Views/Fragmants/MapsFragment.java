package com.sandile.vanguard.Views.Fragmants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBNode;
import com.sandile.vanguard.PlaceDetails;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.Views.Activites.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private ProgressBar pb_loading;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private GoogleMap mMap;

    private View mapView;
    private SupportMapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;

    LatLng origin = new LatLng(-29.785511420324436, 31.039100258138003);
    LatLng dest = new LatLng(-29.78178672728432, 31.045313196592897);
    PlacesClient placesClient;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    Button btn_find;
    Button btn_search;
    private TextView tv_isMetric;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latLng.latitude, latLng.longitude))
                                        .title("Marker"));
                            }
                        });
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {

                                return false;
                            }
                        });
                    }
                } else {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }

            }
        }
        else {
            mMap.setMyLocationEnabled(false);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        new SnackTwo().redSnack(getActivity(), connectionResult.getErrorMessage());
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if(getLocationPermission()){
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
                mapView = mapFragment.getView();
            }
            else{
                new SnackTwo().redSnack(this.getActivity(), "Map is not ready :-(");
                return new View(this.getContext());
            }
        }
        else{
            new SnackTwo().redSnack(this.getActivity(), "Accept the permissions");
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pb_loading = getView().findViewById(R.id.maps_pb_loading);


    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Places.initialize(view.getContext(), "AIzaSyANaY7LDVroXKDyKlUKiIIg6oAUeIOCDbw");

        tv_isMetric = view.findViewById(R.id.map_tv_isMetric);
        try{
            if(Profile.userDetailGlobal.getIsMetric()){
                tv_isMetric.setVisibility(View.VISIBLE);
                tv_isMetric.setText("Metric system");
            }
            else if(!Profile.userDetailGlobal.getIsMetric()){
                tv_isMetric.setVisibility(View.VISIBLE);
                tv_isMetric.setText("Imperial system");
            }
        }
        catch (Exception e){

        }

        btn_search = view.findViewById(R.id.search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(latLngTemp).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTemp, 6));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                new SnackTwo().redSnack(getActivity(), status.getStatusMessage());
            }
            return;
        }
    }

    private void showLandmarkDetailsDialog(PlaceDetails inPlaceDetails){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(inPlaceDetails.getName()+" details");
        builder.setMessage("Address: " + inPlaceDetails.getAddress()+
                "\n\nId: " + inPlaceDetails.getId());
        builder.setNegativeButton("Ok", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Boolean getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        return false;
    }



}