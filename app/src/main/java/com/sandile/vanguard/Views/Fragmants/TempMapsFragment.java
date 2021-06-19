package com.sandile.vanguard.Views.Fragmants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;

import org.jetbrains.annotations.NotNull;

public class TempMapsFragment extends Fragment implements OnMapReadyCallback {

    private GeoApiContext geoApiContext = null;
    private GoogleMap mMap;
    private LatLng currentUserLocation;
    private static FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Setting up the button!
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new SnackTwo().redSnack(getActivity(), "Turn location permission on!");//TODO: ask for permission properly!
            return;
        }
            mMap.setMyLocationEnabled(true);//If GPS is off nothing will happen!

        //Getting user current location.
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    new SnackTwo().redSnack(getActivity(), "ask for permission!!!");
                }
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Location> task) {
                        if(task.isSuccessful()){
                            currentUserLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());

                            mMap.addMarker(new MarkerOptions()
                                    .position(currentUserLocation)
                                    .title(currentUserLocation.toString()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentUserLocation));
                            mMap.setMaxZoomPreference(30);
                        }
                        else{
                            new SnackTwo().redSnack(getActivity(), "Could not get you last location!!!");
                        }
                    }
                });
                return false;
            }
        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title("Marker"));

//                calculateDirections(latLng);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.temp_maps_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }




//        if(geoApiContext == null){
//            geoApiContext = new GeoApiContext.Builder()
//                    .apiKey(getString(R.string.google_api_key))
//                    .build();
//        }


//        DirectionApiRequest directionRequest = new DirectionApiRequest();
    }

    private void calculateDirections(LatLng latLng){
        Log.d("TempMapsFragment.java", "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                latLng.latitude,
                latLng.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
//                        mUserPosition.getGeo_point().getLatitude(),
//                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d("TempMapsFragment.java", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("TempMapsFragment.java", "onResult: routes: " + result.routes[0].toString());
                Log.d("TempMapsFragment.java", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("TempMapsFragment.java", "onFailure: " + e.getMessage() );

            }
        });
    }



}