package com.durgesh.restaurant.ui.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.models.googlePlaces.RootGooglePlaces;
import com.durgesh.restaurant.ui.BlinkingMarker;
import com.durgesh.restaurant.ui.details.DetailsActivity;
import com.durgesh.restaurant.ui.home.HomeContract;
import com.durgesh.restaurant.ui.map.NearbyPlacesAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.DaggerFragment;
import retrofit2.Response;

/**
 * Created by Snehal Tembare on 5/9/17.

 */
public class MapFragment extends DaggerFragment implements HomeContract.MapView, OnMapReadyCallback {


    private static final double DIST_IN_MILE = 1609;
    private static final float STROKE_WIDTH = 2;

    @Inject
    HomeContract.MapPresenter mapPresenter;

    @Inject
    public MapFragment(){
    }

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.progress_bar_maps)
    protected ProgressBar mProgressBar;

    @BindView(R.id.frame_layout_maps)
    protected FrameLayout mFrameLayout;

    private double srcLat = 18.499502;

    private double srcLng = 73.821873;

    private NearbyPlacesAdapter mNearbyPlacesAdapter;

    private int selectedCardPos = 0;

    GoogleMap mMap;

    private BlinkingMarker mBlinkingMarker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mapPresenter.takeView(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        mapPresenter.takeView(this);
        try {
            view = inflater.inflate(R.layout.activity_maps, container, false);
            ButterKnife.bind(this, view);

        } catch (InflateException e) {
            return view;
        }
        createMapView(savedInstanceState, view);
        nearbyPlaces(srcLat, srcLng);
        return view;
    }

    public void setMapMarker(
            final List<Result> resultArrayList, final RootGooglePlaces rootGooglePlaces){
        for (int i = 0; i < 20; i++) {
            Double lat = Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLat());
            Double lng = Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLng());
            String placeName = resultArrayList.get(i).getName();

            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_no_disc_marker));
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final LatLng markerPosition = marker.getPosition();
                int selected_marker = -1;
                for (int i = 0; i < rootGooglePlaces.getResults().size(); i++) {
                    if (markerPosition.latitude == Double.parseDouble(
                            rootGooglePlaces.getResults().get(i).getGeometry().getLocation().getLat())
                            && markerPosition.longitude == Double.parseDouble(
                            rootGooglePlaces.getResults().get(i).getGeometry().getLocation().getLng())) {
                        selected_marker = i;
                    }
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(markerPosition).zoom(12).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                mRecyclerView.smoothScrollToPosition(selected_marker);
                marker.showInfoWindow();
                return false;
            }
        });
    }

    void createMapView(Bundle savedInstanceState, View view) {
        //        getActivity().getActionBar().hide();
        MapView mapFragment =  view.findViewById(R.id.maps);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);
        updateView(View.VISIBLE,(float)0.5);
    }

    private void nearbyPlaces(double srcLat, double srcLng) {
        mapPresenter.nearbyPlaces( srcLat, srcLng);
    }

    public void createSnapHelper() {
        //scroll one item at a time
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mNearbyPlacesAdapter);
    }

    @Override
    public void updateView(int visibility, float alpha) {
        mProgressBar.setVisibility(visibility);
        mFrameLayout.setAlpha(alpha);
    }
    public void createLayoutManager(final Response<RootGooglePlaces> response) {
        final LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,
                false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
               setBlinker(linearLayoutManager, response);
            }
        });
    }

    @Override
    public Activity activity() {
        return getActivity();
    }

    public void setBlinker(LinearLayoutManager linearLayoutManager, Response<RootGooglePlaces> response ) {
        int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
        int lastPos = linearLayoutManager.findLastVisibleItemPosition();
        int middle = Math.abs(lastPos - firstPos) / 2 + firstPos;

        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_disc_marker);
        mBlinkingMarker = new BlinkingMarker(bitmap, mMap, 10, 2000);
        mBlinkingMarker.addToMap(new LatLng(Double.parseDouble(response.body().getResults().
                get(middle).getGeometry().getLocation().getLat()),
                Double.parseDouble(response.body().getResults().get(middle).getGeometry().
                        getLocation().getLng())));

        mBlinkingMarker.startBlinking();
        mNearbyPlacesAdapter.notifyDataSetChanged();
    }

    public void setNearbyPlacesList(Response<RootGooglePlaces> response, List<Place> placeArrayList) {
        mNearbyPlacesAdapter = new NearbyPlacesAdapter(getActivity(),
                selectedCardPos, placeArrayList, response.body().getResults(),
                response.body(),
                new NearbyPlacesAdapter.OnCardItemClickListener() {
                    @Override
                    public void onCardClick(int position, Result results) {
                        selectedCardPos = position;

                        startActivity(new Intent(getActivity(), DetailsActivity.class));
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng currentLoc = new LatLng(srcLat, srcLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(getString(R.string.current_loc));
        mMap.addMarker(new MarkerOptions().position(currentLoc).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(srcLat, srcLng))
                .radius(DIST_IN_MILE)
                .strokeWidth(STROKE_WIDTH)
                .strokeColor(ContextCompat.getColor(getActivity(), R.color.colorMapCircleStroke))
                .fillColor(ContextCompat.getColor(getActivity(), R.color.colorMapCircle)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14));
    }

    @OnClick(R.id.imgCurLoc)
    public void imgCurLoc(View view) {
        com.durgesh.restaurant.app.constant.RToast.showToast(getActivity(), "change location");
    }

    @OnClick(R.id.imgFilter)
    public void imgFilters(View view) {
        com.durgesh.restaurant.app.constant.RToast.showToast(getActivity(), "filters");
    }

}