package com.sandile.vanguard.Views.Fragmants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.Unit;
import com.sandile.vanguard.CustomPlace;
import com.sandile.vanguard.MapHelper;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    //Pallets
    private FloatingActionButton fab_search, fab_direction, fab_nearby, fab_mapLayer, fab_share;
    private AppBarLayout tb_toolbar;
    private TextView tv_time, tv_distance;
    private ImageView iv_stopNav;

    //Map
    private GeoApiContext geoApiContext = null;
    private static GoogleMap mMap;
    private static FusedLocationProviderClient fusedLocationClient;
    private Marker currentUserMarker;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int currentMapLayer = GoogleMap.MAP_TYPE_NORMAL;
    private Marker tempMarker = null;
    private Boolean isNavMode = false;//This is changed in enterNavigationMode()
    private Polyline mMapPolyline = null;

    private static PlacesSearchResult[] tempNearbyPlaces;

    //User
    public static LatLng currentUserLocation = null, destinationLatLng = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(currentMapLayer);
        mMap.getUiSettings().setZoomControlsEnabled(true);

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
                setUserCurrentLocation();
                return false;
            }
        });

        //Click on map and add marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {

                if (!isNavMode) {
                    fab_direction.setVisibility(View.INVISIBLE);
                    fab_share.setVisibility(View.INVISIBLE);

                    if (tempMarker != null) {
                        tempMarker.remove();
                    }

                    tempMarker = mMap.addMarker(new MarkerOptions()
                            .title("Pin")
                            .snippet("Click to view details")
                            .position(latLng)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                destinationLatLng = marker.getPosition();
                fab_direction.setVisibility(View.VISIBLE);
                fab_share.setVisibility(View.VISIBLE);

                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull @NotNull Marker marker) {

                new SnackTwo().greenSnack(getActivity(), "Getting details");

                try {
                    GeocodingResult[] placeIdApi = GeocodingApi.newRequest(geoApiContext)
                            .latlng(new com.google.maps.model.LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                            .await();

                    PlaceDetails details = PlacesApi.placeDetails(geoApiContext, placeIdApi[0].placeId).await();

                    showPlaceDetailsDialog(details);

                } catch (ApiException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
                    e.printStackTrace();
                }

            }
        });


        if (currentUserLocation == null) {
            setUserCurrentLocation();
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(currentUserLocation)
                    .title("Me")
                    .snippet("Click to view details")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentUserLocation)
                    .zoom(15)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

//        if (tempNearbyPlaces != null) {
//            try {
//                nearbyPlacesSetup(tempNearbyPlaces);
//            } catch (IOException e) {
//                new SnackTwo().redSnack(getActivity(), e.getMessage());
//            }
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (geoApiContext == null) {//context setup
            geoApiContext = new GeoApiContext.Builder()
//                    .apiKey(getString(R.string.google_api_key))
                    .apiKey("AIzaSyANaY7LDVroXKDyKlUKiIIg6oAUeIOCDbw")
                    .build();
        }

        Places.initialize(view.getContext(), getString(R.string.google_api_key));

        palletsSetup(view);

    }

    private void showPlaceDetailsDialog(PlaceDetails details) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);

        builder.setTitle(details.name);

        builder.setMessage(new CustomPlace().FormatPlaceDetails(details));

        builder.setPositiveButton("Add to fav",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try{
                            saveLocation(new CustomPlace(details.formattedAddress, details.placeId, details.name, details.geometry.location.lat, details.geometry.location.lng));
                            new SnackTwo().greenSnack(getActivity(), "Place added!");

                        }catch (Exception e){
                            new SnackTwo().redSnack(getActivity(), e.toString());
                        }

                    }
                });
        builder.setNegativeButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sharePlaceDetails(new CustomPlace().FormatPlaceDetails(details));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveLocation(CustomPlace customPlace) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(UserDetail.currentUserId).child("locations").child(customPlace.getId())
                .setValue(customPlace).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                new SnackTwo().greenSnack(getActivity(), "Location has been added to your list!");
            }
            else{
                new SnackTwo().redSnack(getActivity(), "Something went wrong\nCould not save ");
            }
        }).addOnFailureListener(e -> {
            new SnackTwo().redSnack(getActivity(), e.getMessage());
        });
    }

    private void enterNavigationMode(boolean isEnter) {
        if (isEnter) {
            isNavMode = true;
            //App bar
            tb_toolbar.setVisibility(View.VISIBLE);

            if (mMapPolyline != null) {
                mMapPolyline.remove();
            }

            //Location btn
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(false);

            //Search btn
            fab_mapLayer.setVisibility(View.INVISIBLE);
            fab_mapLayer.setVisibility(View.INVISIBLE);
            fab_search.setVisibility(View.INVISIBLE);
            fab_nearby.setVisibility(View.INVISIBLE);
        } else {
            isNavMode = false;

            //App bar
            tb_toolbar.setVisibility(View.GONE);

            //Location btn
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            //Search btn
            fab_mapLayer.setVisibility(View.VISIBLE);
            fab_search.setVisibility(View.VISIBLE);
            fab_nearby.setVisibility(View.VISIBLE);
        }
    }

    private void palletsSetup(View view) {
        fab_search = view.findViewById(R.id.maps_fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        fab_direction = view.findViewById(R.id.maps_fab_direction);
        fab_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng tempDestination = destinationLatLng;

                if (tempDestination != null) {
                    fab_direction.setVisibility(View.INVISIBLE);
                    new SnackTwo().greenSnack(getActivity(), "Getting direction...");
                    calculateDirections(tempDestination);
                    tb_toolbar.setVisibility(View.VISIBLE);
                } else {
                    new SnackTwo().redSnack(getActivity(), "Click on map to choose destination");
                    fab_direction.setVisibility(View.INVISIBLE);
                }
            }
        });

        fab_nearby = view.findViewById(R.id.maps_fab_nearby);
        fab_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SnackTwo().greenSnack(getActivity(), "Showing nearby " + UserDetail.userSessionDetails.getPreferredLandmarkType() + "...");

                try {
                    String tempUserDetailPlaceType = new UserDetail().getUserSessionDetails().getPreferredLandmarkType().toUpperCase();
                    PlaceType tempPlaceType = PlaceType.valueOf(tempUserDetailPlaceType);

                    viewNearbyPlaces(tempPlaceType, new com.google.maps.model.LatLng(currentUserLocation.latitude, currentUserLocation.longitude));

                } catch (InterruptedException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
//                    e.printStackTrace();
                } catch (ApiException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
//                    e.printStackTrace();
                } catch (IOException e) {
                    new SnackTwo().redSnack(getActivity(), e.getMessage());
//                    e.printStackTrace();
                }
            }
        });

        fab_mapLayer = view.findViewById(R.id.map_fab_mapLayer);
        fab_mapLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i <= 4; i++) {
                    if (currentMapLayer == i) {
                        currentMapLayer = i + 1;
                        mMap.setMapType(currentMapLayer);
                        break;
                    } else if (currentMapLayer == 4) {
                        currentMapLayer = 0;
                        mMap.setMapType(currentMapLayer);
                        break;
                    }
                }

            }
        });

        fab_share = view.findViewById(R.id.map_fab_share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SnackTwo().redSnack(getActivity(), "FAB not setup, method is here");
                //todo: pass in the details of the clicked place here
                //you can use try{} to prevent errors
//                sharePlaceDetails();
            }
        });

        iv_stopNav = view.findViewById(R.id.maps_iv_stopNav);
        iv_stopNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapPolyline.remove();
                enterNavigationMode(false);
            }
        });

        tb_toolbar = view.findViewById(R.id.appBarLayout);
        tv_time = view.findViewById(R.id.maps_tv_toolbar_time);
        tv_distance = view.findViewById(R.id.maps_tv_toolbar_distance);

    }

    private void sharePlaceDetails(String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(intent, "Share place details"));
    }

    private void viewNearbyPlaces(PlaceType userPlaceType, com.google.maps.model.LatLng userLocation) throws InterruptedException, ApiException, IOException {
        PlacesSearchResult[] placesSearchResults = new MapHelper().nearbyPlaces(geoApiContext, userLocation, userPlaceType).results;

        if (placesSearchResults != null) {

            if (tempNearbyPlaces != null) {
                tempNearbyPlaces = null;
            }

            tempNearbyPlaces = placesSearchResults;

            nearbyPlacesSetup(tempNearbyPlaces);
        } else {
            new SnackTwo().redSnack(getActivity(), "No nearby places of type" + userPlaceType);
        }
    }

    //This should take PlacesSearchResult[] list and put pins(with name and address) on map
    public void nearbyPlacesSetup(PlacesSearchResult[] placesSearchResults) throws IOException {
        if (placesSearchResults != null) {

            //Move camera to the first result and zoom
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(placesSearchResults[0].geometry.location.lat, placesSearchResults[0].geometry.location.lng))
                    .zoom(13)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            for (PlacesSearchResult onePlacesResult : placesSearchResults) {
                String tempName = "No name";
                String tempAddress = "Lat/lng: " + onePlacesResult.geometry.location.toString();

                if (onePlacesResult.name != null) {
                    tempName = onePlacesResult.name;
                } else if (onePlacesResult.formattedAddress != null) {
                    tempAddress = onePlacesResult.formattedAddress;
                }

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(onePlacesResult.geometry.location.lat, onePlacesResult.geometry.location.lng))
                        .snippet(tempAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(tempName));

            }
        } else {
            new SnackTwo().orangeSnack(getActivity(), "There are no nearby places for " + UserDetail.userSessionDetails.getPreferredLandmarkType());
        }
    }

    //This is used for google search, then placing pin on map
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLngTemp = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLngTemp)
                        .title(place.getName())
                        .snippet(place.getAddress())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTemp, 15));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                new SnackTwo().redSnack(getActivity(), status.getStatusMessage());
            }
            return;
        }
    }

    private void setUserCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new SnackTwo().redSnack(getActivity(), "ask for permission!!!");
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Location> task) {
                if (task.isSuccessful()) {

                    try {
                        currentUserLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    } catch (Exception e) {
                        new SnackTwo().redSnack(getActivity(), "Please turn on your location services!");
                        return;
                    }

                    if (currentUserMarker != null) {
                        currentUserMarker.remove();
                    }

                    currentUserMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentUserLocation)
                            .title("Me")
                            .snippet("Click to view details")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentUserLocation)
                            .zoom(15)                   // Sets the zoom
//                                    .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    new SnackTwo().redSnack(getActivity(), "Failed to get your last location!");
                }
            }
        });
    }

    private void calculateDirections(LatLng latLng) {
        enterNavigationMode(true);
        tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

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

        try {
            Boolean tempIsMetric = new UserDetail().getUserSessionDetails().getIsMetric();
            if (tempIsMetric) {
                directions.units(Unit.METRIC);
            } else {
                directions.units(Unit.IMPERIAL);
            }
        } catch (Exception e) {
            new SnackTwo().redSnack(getActivity(), "You are not signed in!");
        }

        Log.d("TempMapsFragment.java", "calculateDirections: destination: " + destination.toString());

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d("TempMapsFragment.java", "onResult: routes: " + result.routes[0].toString());
                Log.d("TempMapsFragment.java", "onResult: distance: " + result.routes[0].legs[0].distance);
                Log.d("TempMapsFragment.java", "onResult: Duration: " + result.routes[0].legs[0].duration);
                Log.d("TempMapsFragment.java", "onResult: End address: " + result.routes[0].legs[0].endAddress);
                Log.d("TempMapsFragment.java", "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        tempMarker.setTitle("Destination");
                        tv_time.setText(result.routes[0].legs[0].duration.toString());
                        tv_distance.setText(result.routes[0].legs[0].distance.toString());
                        tempMarker.setSnippet(result.routes[0].legs[0].endAddress);
                    }
                });

                if (mMapPolyline != null) {
                    mMapPolyline.remove();
                }

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {

                new SnackTwo().redSnack(getActivity(), e.getMessage());
            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("TempMapsFragment.java", "run: result routes: " + result.routes.length);

                for (DirectionsRoute route : result.routes) {
                    Log.d("TempMapsFragment.java", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    mMapPolyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    mMapPolyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue_500));

                }
            }
        });
    }


}