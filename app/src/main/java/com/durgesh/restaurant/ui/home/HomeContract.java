package com.durgesh.restaurant.ui.home;

/**
 * Created by durgeshtrivedi on 15/12/17.
 */

import android.location.Address;
import android.location.Location;

import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.models.googlePlaces.RootGooglePlaces;
import com.durgesh.restaurant.ui.BasePresenter;
import com.durgesh.restaurant.ui.BaseView;

import java.util.List;

import retrofit2.Response;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface HomeContract {


    interface HomeView extends BaseView<Presenter> {

        void showPopUpMenu();

        void updateAddress(List<Address> addresses);

        void showProgressDialog();

        void dissmissDialog();

        void updateRestaurantCount(int count);

        void updateView();

        void loadHomeList(List<Place> placeArrayList);
    }

    interface Presenter extends BasePresenter<HomeView> {

        void getUserLocation();

        void getPlaces(double lat, double lng);

         double getLatitude();

         void setLatitude(double latitude);

         double getLongitude();

         void setLongitude(double longitude);

         Location getLocation();

         void setLocation(Location location);

    }

    interface MapView extends BaseView<MapPresenter> {
        void updateView(int visibility, float alpha);

        void createLayoutManager(final Response<RootGooglePlaces> response);

        void setNearbyPlacesList(Response<RootGooglePlaces> response, List<Place> placeList);

        void setMapMarker(
                final List<Result> resultArrayList, final RootGooglePlaces rootGooglePlaces);

        void createSnapHelper();
    }

    interface MapPresenter extends BasePresenter<MapView> {
        void nearbyPlaces(double srcLat, double srcLng);
    }
}

