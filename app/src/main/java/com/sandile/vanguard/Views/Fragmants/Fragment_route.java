package com.sandile.vanguard.Views.Fragmants;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.directionhelpers.FetchURL;
import com.sandile.vanguard.directionhelpers.TaskLoadedCallback;

public class Fragment_route extends Fragment implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    Button btn_route;
    private Polyline currentPolyline;

    private SupportMapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapRoute);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        else{
            new SnackTwo().redSnack(this.getActivity(), "Map is not ready :-(");
            return new View(this.getContext());
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setContentView(R.layout.fragment_route);
        btn_route = getActivity().findViewById(R.id.route_btn_findRoute);
        btn_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchURL(getContext()).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            }
        });

        place1 = new MarkerOptions().position(new LatLng(27.658143, 85.3199503)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(27.667491, 85.3208583)).title("Location 2");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(place1);
        mMap.addMarker(place2);


    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBWXJMKMfxvqQvK6lf2Qjo1VbI5hxBm-R4";
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key=AIzaSyCG4OgpOZl377GsvkM9or9QWee0I2h31UM";
        return url;
    }

















    //Built in
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_route() {
        // Required empty public constructor
    }

    public static Fragment_route newInstance(String param1, String param2) {
        Fragment_route fragment = new Fragment_route();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
}