package com.durgesh.restaurant.ui.home;

/**
 * Created by durgeshtrivedi on 15/12/17.
 */

import android.location.Location;
import android.support.annotation.Nullable;

import com.durgesh.restaurant.dagger.ActivityScoped;
import com.durgesh.restaurant.ui.home.fragments.HomeListFragment;

import javax.inject.Inject;

/**
 * Listens to user actions from the UI ({@link HomeListFragment }), retrieves the data and updates the
 * UI as required.
 * <p/>
 * By marking the constructor with {@code @Inject}, Dagger injects the dependencies required to
 * create an instance of the HomePresenter (if it fails, it emits a compiler error).  It uses
 * {@link HomeModule} to do so.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 **/
@ActivityScoped
final class HomePresenter implements HomeContract.Presenter {

    private final HomeInteractor mHomeInteractor;

    private double latitude;

    private double longitude;

    private Location location;

    @Nullable
    private HomeContract.HomeView homeView;

    @Inject
    public HomePresenter(HomeInteractor homeInteractor) {
        this.mHomeInteractor = homeInteractor;
    }

    @Override
    public void takeView(HomeContract.HomeView view) {
        this.homeView = view;
    }

    @Override
    public void dropView() {
        homeView = null;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void getUserLocation() {
        mHomeInteractor.setHomeView(homeView);
        mHomeInteractor.getUserLocation();
    }

    public void getPlaces(double lat, double lng) {
        mHomeInteractor.setHomeView(homeView);
        mHomeInteractor.getPlaces(lat, lng);
    }


}