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
    //TODO: move nearbyPlaces() here and calculateDirections(LatLng latLng)

    //todo: pass in a place to search
    public PlacesSearchResponse nearbyPlaces(GeoApiContext geoApiContext, LatLng userLocation, PlaceType placeType) {

        //TODO: change userLocation
        NearbySearchRequest req = PlacesApi.nearbySearchQuery(geoApiContext, new com.google.maps.model.LatLng(userLocation.lat, userLocation.lng));

        try {
            PlacesSearchResponse resp = req
                    .type(placeType)
                    .radius(10000)
                    .await();
            return resp;
        }
        catch (IOException | InterruptedException | ApiException e) {
            e.printStackTrace();

        }
        return null;
    }
}
