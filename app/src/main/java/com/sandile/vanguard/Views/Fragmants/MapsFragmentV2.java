package com.sandile.vanguard.Views.Fragmants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.Unit;
import com.sandile.vanguard.MapHelper;
import com.sandile.vanguard.MetricToImperial;
import com.sandile.vanguard.R;
import com.sandile.vanguard.SnackTwo;
import com.sandile.vanguard.UserDetail;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapsFragmentV2 extends Fragment implements OnMapReadyCallback {

    //Pallets
    private FloatingActionButton fab_search, fab_direction, fab_nearby;
    private AppBarLayout tb_toolbar;
    private TextView tv_time, tv_distance;
    private ImageView iv_stopNav;

    private GeoApiContext geoApiContext = null;
    private GoogleMap mMap;
    public LatLng currentUserLocation = null, destinationLatLng = null;
    private static FusedLocationProviderClient fusedLocationClient;
    private Marker currentUserMarker, destinationMarker;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

        //User clicks on map and I will navigate them there
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull @NotNull LatLng latLng) {

                destinationMarker = mMap.addMarker(new MarkerOptions()
                        .snippet("Pin")
                        .snippet("lat: " + latLng.latitude+"lng: " + latLng.longitude)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .position(new LatLng(latLng.latitude, latLng.longitude)));

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {

                if(destinationMarker != null){
                    destinationMarker.remove();
                }
                destinationMarker = mMap.addMarker(new MarkerOptions()
                        .snippet("Pin")
                        .snippet("lat: " + marker.getPosition().latitude+"lng: " + marker.getPosition().longitude)
                        .position(marker.getPosition()));

                //Setting up destination
                destinationLatLng = marker.getPosition();

                fab_direction.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull @NotNull Marker marker) {
                new SnackTwo().orangeSnack(getActivity(), "Info window clicked");
            }
        });




        setUserCurrentLocation();
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

        //Map setup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (geoApiContext == null) {//context setup
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_api_key))
                    .build();
        }

        Places.initialize(view.getContext(), getString(R.string.google_api_key));

        palletsSetup(view);

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

                if(tempDestination != null){
                    fab_direction.setVisibility(View.INVISIBLE);
                    new SnackTwo().greenSnack(getActivity(),"Getting direction...");
                    calculateDirections(tempDestination);
                    tb_toolbar.setVisibility(View.VISIBLE);
                }
                else{
                    new SnackTwo().redSnack(getActivity(),"Click on map to choose destination");
                    fab_direction.setVisibility(View.INVISIBLE);
                }
            }
        });

        fab_nearby = view.findViewById(R.id.maps_fab_nearby);
        fab_nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SnackTwo().greenSnack(getActivity(),"Showing nearby places...");

                UserDetail tempUserDetail =  new UserDetail().getUserSessionDetails();

                try {
                    PlaceType tempPlaceType = PlaceType.valueOf(tempUserDetail.getPreferredLandmarkType().toUpperCase());

                    viewNearbyPlaces(tempPlaceType);
                } catch (InterruptedException e) {
                    new SnackTwo().redSnack(getActivity(),e.getMessage());
//                    e.printStackTrace();
                } catch (ApiException e) {
                    new SnackTwo().redSnack(getActivity(),e.getMessage());
//                    e.printStackTrace();
                } catch (IOException e) {
                    new SnackTwo().redSnack(getActivity(),e.getMessage());
//                    e.printStackTrace();
                }
            }
        });

        iv_stopNav = view.findViewById(R.id.maps_iv_stopNav);
        iv_stopNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SnackTwo().greenSnack(getActivity(),"Nav is stopping");
            }
        });

        tb_toolbar = view.findViewById(R.id.appBarLayout);
        tv_time = view.findViewById(R.id.maps_tv_toolbar_time);
        tv_distance = view.findViewById(R.id.maps_tv_toolbar_distance);

    }

    //TODO: pass in current user location, PLACE_TYPE
    private void viewNearbyPlaces(PlaceType userPlaceType) throws InterruptedException, ApiException, IOException {
        PlacesSearchResult[] placesSearchResults = new MapHelper().nearbyPlaces(geoApiContext, new com.google.maps.model.LatLng(currentUserLocation.latitude, currentUserLocation.longitude), userPlaceType).results;

        if(placesSearchResults != null){
            Log.e("response1Tag", placesSearchResults[0].toString());
            Log.e("response2Tag", placesSearchResults[1].toString());

            double lat1 = placesSearchResults[0].geometry.location.lat;
            double lng1 = placesSearchResults[0].geometry.location.lng;

            double lat2 = placesSearchResults[1].geometry.location.lat;
            double lng2 = placesSearchResults[1].geometry.location.lng;

            double lat3 = placesSearchResults[3].geometry.location.lat;
            double lng3 = placesSearchResults[3].geometry.location.lng;

            double lat4 = placesSearchResults[4].geometry.location.lat;
            double lng4 = placesSearchResults[4].geometry.location.lng;

            //TODO: PUT THIS IN A METHOD, GETTING DETAILS OF A PLACE

            PlaceDetails details = PlacesApi.placeDetails(geoApiContext, placesSearchResults[0].placeId).await();

            String name = details.name;
            String address = details.formattedAddress;
            URL icon = details.icon;
            double lat = details.geometry.location.lat;
            double lng = details.geometry.location.lng;
            String vicinity = details.vicinity;
            String placeId = details.placeId;
            String phoneNum = details.internationalPhoneNumber;
            String[] openHours = details.openingHours!=null ? details.openingHours.weekdayText : new String[0];
            String hoursText = "";
            for(String sv : openHours) {
                hoursText += sv + "\n";
            }
            float rating = details.rating;

            String content = address + "\n" +
                    "Place ID: " + placeId + "\n" +
                    "Rating: " + rating + "\n" +
                    "Phone: " + phoneNum + "\n" +
                    "Open Hours: \n" + hoursText;

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat1, lng1))
                    .snippet(content)
                    .title(name));

            //TODO: PUT THIS IN A METHOD, GETTING DETAILS OF A PLACE

//            mMap.addMarker(new MarkerOptions().position(new LatLng(lat1, lng1))).setTitle("marker 1");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat2, lng2))).setTitle("marker 2");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat3, lng3))).setTitle("marker 3");
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat4, lng4))).setTitle("marker 4");

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat1, lng1)));
        }
        else{
            new SnackTwo().redSnack(getActivity(),"Could not find any places");
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
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTemp, 15));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                new SnackTwo().redSnack(getActivity(), status.getStatusMessage());
            }
            return;
        }
    }

    private void setUserCurrentLocation(){
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

                    currentUserMarker.setTag("This is a tag");

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

        try{
            Boolean tempIsMetric = new UserDetail().getUserSessionDetails().getIsMetric();
            if(tempIsMetric){
                directions.units(Unit.METRIC);
            }
            else{
                directions.units(Unit.IMPERIAL);
            }
        }catch (Exception e) {
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
                        destinationMarker.setTitle("Destination");
                        tv_time.setText(result.routes[0].legs[0].duration.toString());
                        tv_distance.setText(result.routes[0].legs[0].distance.toString());
                        destinationMarker.setSnippet(result.routes[0].legs[0].endAddress);
                    }
                });

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
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.blue_500));
//                    polyline.setClickable(true); TODO: clicking polyline

                }
            }
        });
    }


}