package com.durgesh.restaurant.ui.home;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.OpeningHours;
import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.network.ApiHelper;
import com.durgesh.restaurant.ui.BaseView;
import com.durgesh.restaurant.ui.home.fragments.HomeListFragment;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by durgeshtrivedi on 20/12/17.
 */

public class Interacter {

    protected Context context;

    protected ApiHelper service;

    protected FusedLocationProviderClient mFusedLocationProviderClient;

    protected Location mLastKnownLocation;

    protected BaseView view;

    protected List<Place> createPlaceList(List<Result> resultArrayList) {
        List <Place> placeArrayList = new ArrayList<>();
        if (resultArrayList != null && resultArrayList.size() != 0) {
            for (int index = 0; index < (resultArrayList.size() - 1); index++) {
                Location destination = getLocation(index, resultArrayList);
                OpeningHours openingHours = getOpeningHours(index, resultArrayList);
                Place place = getPlace(index, destination,resultArrayList, openingHours);
                placeArrayList.add(place);
            }
        } else {
            Toast.makeText(view.activity(), R.string.data_not_found, Toast.LENGTH_LONG).show();
        }
        return placeArrayList;
    }

    protected Place getPlace(int index, Location destination, List<Result> resultArrayList, OpeningHours openingHours) {
        Place place ;
        if (resultArrayList.get(index) != null && resultArrayList.get(index).getPhotos() != null &&
                resultArrayList.get(index).getPhotos().get(0) != null &&
                resultArrayList.get(index).getPhotos().get(0).getPhoto_reference() != null
                && openingHours != null) {
            place = new Place(resultArrayList.get(index).getName(),
                    resultArrayList.get(index).getRating(), null,
                    destination.distanceTo(mLastKnownLocation),
                    resultArrayList.get(index).getPhotos().get(0).getPhoto_reference(),
                    openingHours);
        } else {
            place = new Place(resultArrayList.get(index).getName(),
                    resultArrayList.get(index).getRating(), null,
                    destination.distanceTo(mLastKnownLocation),
                    null, null);
        }
        return place;
    }




    protected OpeningHours getOpeningHours(int index, List<Result> resultArrayList) {
        OpeningHours openingHours;
        if (resultArrayList.get(index).getOpening_hours() != null) {
            if (resultArrayList.get(index).getOpening_hours().getWeekday_text() != null) {
                openingHours = new OpeningHours(resultArrayList.get(index).getOpening_hours().isOpen_now(),
                        resultArrayList.get(index).getOpening_hours().getWeekday_text());
            } else {
                openingHours = new OpeningHours(resultArrayList.get(index).getOpening_hours().isOpen_now(),
                        null);
            }

        } else {
            openingHours = new OpeningHours(true, null);
        }

        return openingHours;
    }

    protected Location getLocation(int index,List<Result> resultArrayList ) {
        Location destination =  new Location("Destination");
        Double lat = Double.parseDouble(resultArrayList.get(index).getGeometry().getLocation().getLat());
        Double lng = Double.parseDouble(resultArrayList.get(index).getGeometry().getLocation().getLng());
        destination.setLatitude(lat);
        destination.setLongitude(lng);
        Log.v(HomeListFragment.HomeListConstant.TAG, "******" + resultArrayList.get(0).getName());
        return destination;
    }
}
