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
import android.os.Handler;
import android.os.Looper;
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
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TempMapsFragment extends Fragment implements OnMapReadyCallback {

    private GeoApiContext geoApiContext = null;
    private GoogleMap mMap;
    public LatLng currentUserLocation;
    private static FusedLocationProviderClient fusedLocationClient;
    private Marker currentUserMarker, destinationMarker;

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

                            currentUserMarker = mMap.addMarker(new MarkerOptions()
                                    .position(currentUserLocation)
                                    .title("Me"));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(currentUserLocation)      // Sets the center of the map to Mountain View
                                    .zoom(20)                   // Sets the zoom
//                                    .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                new SnackTwo().greenSnack(getActivity(),"Getting direction...");

                calculateDirections(latLng);

                destinationMarker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(latLng.latitude, latLng.longitude)));

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

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }

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
                        currentUserLocation.latitude,
                        currentUserLocation.longitude
                )
        );
        Log.d("TempMapsFragment.java", "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("TempMapsFragment.java", "onResult: routes: " + result.routes[0].toString());
                Log.d("TempMapsFragment.java", "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d("TempMapsFragment.java", "onResult: Duration: " + result.routes[0].legs[0].duration);
                Log.d("TempMapsFragment.java", "onResult: Duration: " + result.routes[0].legs[0].endAddress);
                Log.d("TempMapsFragment.java", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                 destinationMarker.setTitle(result.routes[0].legs[0].endAddress);

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                new SnackTwo().redSnack(getActivity(), e.getMessage());
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("TempMapsFragment.java", "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d("TempMapsFragment.java", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue_700));
//                    polyline.setClickable(true); TODO: clicking polyline

                }
            }
        });
    }


}