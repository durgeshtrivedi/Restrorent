package com.durgesh.restaurant.ui.home;

import android.support.annotation.Nullable;

import javax.inject.Inject;

/**
 * Created by durgeshtrivedi on 19/12/17.
 */

public class HomeMapPresenter implements HomeContract.MapPresenter{

    @Nullable
    private HomeContract.MapView mapView;

    private final MapIntracter mapInteractor;


    @Inject
    public HomeMapPresenter(MapIntracter mapInteractor) {
        this.mapInteractor = mapInteractor;
    }

    @Override
    public void takeView(HomeContract.MapView view) {
        this.mapView = view;
    }
    @Override
    public void dropView() {

    }

    public void nearbyPlaces(double srcLat, double srcLng){
        mapInteractor.setMapView(mapView);
        mapInteractor.nearbyPlaces(srcLat, srcLng);
    }
}
