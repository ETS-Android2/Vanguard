package com.sandile.vanguard;

import com.google.maps.GeoApiContext;
import com.google.maps.NearbySearchRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;

import java.io.IOException;

public class MapHelper {

    public PlacesSearchResponse nearbyPlaces(GeoApiContext geoApiContext, LatLng userLocation, PlaceType placeType) {

        NearbySearchRequest req = PlacesApi.nearbySearchQuery(geoApiContext, new com.google.maps.model.LatLng(userLocation.lat, userLocation.lng));

        try {
            return req
                    .type(placeType)
                    .radius(10000)
                    .await();
        }
        catch (IOException | InterruptedException | ApiException e) {
            e.printStackTrace();

        }
        return null;
    }
}
