package com.durgesh.restaurant.ui.home;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.durgesh.restaurant.models.googlePlaces.Hotel;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.network.NetworkHelper;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by durgeshtrivedi on 15/12/17.
 */

public class HomeInteractor extends Interactor {

    private HomeContract.Presenter  presenter;

    private HomeContract.HomeView  homeView;

    @Inject
    public HomeInteractor() {

    }
    public void setHomeView(HomeContract.HomeView  homeView) {
        this.homeView = homeView;
        this.view = homeView;
    }
    public void setPresenter(HomeContract.Presenter  presenter) {
        this.presenter = presenter;
    }



    public void getUserLocation() {
        context = homeView.activity().getApplicationContext();
        service  = NetworkHelper.getGoogleClient(context);

        if (NetworkHelper.checkPermission(context)) {
             NetworkHelper.requestPermission(homeView.activity());
        } else {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(homeView.activity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        presenter.setLocation(mLastKnownLocation);
                        Geocoder geocoder = new Geocoder(context);
                        try {

                            Log.v("Lat", "" + mLastKnownLocation.getLatitude());
                            Log.v("Long", "" + mLastKnownLocation.getLongitude());

                            List<Address> addresses = geocoder.getFromLocation(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude(), 1);
                            homeView.updateAddress(addresses);
                            callPlaceAPI( mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }


    public void getPlaces(double lat, double lng) {
        context = homeView.activity().getApplicationContext();
        callPlaceAPI(lat, lng);
    }

    private void callPlaceAPI(double lat, double lng) {
        final Call<Hotel> call = service.searchNearestPlaces(lat
                + "," + lng);
        homeView.showProgressDialog();

        call.enqueue(new Callback<Hotel>() {
            @Override
            public void onResponse(Call<Hotel> call, Response<Hotel> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        homeView.dissmissDialog();
                        Log.v("Retrofit response", "" + response.body().toString());
                        //Place API call
                        List<Result> resultArrayList = response.body().getResults();
                        populateResult(resultArrayList);
                    }
                }
                homeView.dissmissDialog();
            }

            @Override
            public void onFailure(Call<Hotel> call, Throwable t) {
                homeView.dissmissDialog();
            }
        });

    }

    private void populateResult( List<Result> resultArrayList) {
        List <Place> placeList = createPlaceList(resultArrayList);
        int count = placeList.size();
                if (count > 0) {
            Log.v("Size", "" + count);
            homeView.updateRestaurantCount(count);
            homeView.loadHomeList(placeList);
            homeView.updateView();
        }

    }
}
