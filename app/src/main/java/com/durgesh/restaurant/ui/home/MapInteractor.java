package com.durgesh.restaurant.ui.home;

import android.view.View;

import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.models.googlePlaces.RootGooglePlaces;
import com.durgesh.restaurant.network.ApiClient;
import com.durgesh.restaurant.network.ApiHelper;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by durgeshtrivedi on 20/12/17.
 */

public class MapInteractor extends Interactor {

    private HomeContract.MapView  mapView;

    private HomeContract.MapPresenter  mapPresenter;

    @Inject
    public void MapIntracter() {

    }

    public void setHomeView(HomeContract.MapView  mapView) {
        this.mapView = mapView;
        this.view = mapView;
    }

    public void setMapPresenter(HomeContract.MapPresenter  presenter) {
        this.mapPresenter = presenter;
    }

    public void setMapView(HomeContract.MapView  mapView) {
        this.mapView = mapView;
    }

    public void nearbyPlaces(double srcLat, double srcLng) {
        if (ApiClient.getGoogleClient(mapView.activity()) != null) {
            ApiHelper service = ApiClient.getGoogleClient(mapView.activity()).create(ApiHelper.class);
            Call<RootGooglePlaces> call = service.getGooglePlaces(srcLat + "," + srcLng);
            call.enqueue(new Callback<RootGooglePlaces>() {
                @Override
                public void onResponse(Call<RootGooglePlaces> call, final Response<RootGooglePlaces> response) {
                    if (response.isSuccessful()) {
                        mapView.updateView(View.GONE,(float)1.0);
                        RootGooglePlaces rootGooglePlaces = response.body();
                        List<Result> resultArrayList = response.body().getResults();
                        mapView.setMapMarker(resultArrayList, rootGooglePlaces);
                        List<Place> placeArrayList = createPlaceList(resultArrayList);
                        mapView.setNearbyPlacesList(response, placeArrayList);
                        mapView.createLayoutManager(response);
                        mapView.createSnapHelper();
                    }
                }

                @Override
                public void onFailure(Call<RootGooglePlaces> call, Throwable t) {
                    mapView.updateView(View.GONE,(float)1.0);
                }
            });
        }
    }

}
