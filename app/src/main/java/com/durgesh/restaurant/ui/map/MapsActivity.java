package com.durgesh.restaurant.ui.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.models.googlePlaces.Result;
import com.durgesh.restaurant.models.googlePlaces.RootGooglePlaces;
import com.durgesh.restaurant.network.ApiClient;
import com.durgesh.restaurant.network.ApiHelper;
import com.durgesh.restaurant.ui.BlinkingMarker;
import com.durgesh.restaurant.ui.details.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Prajakta Patil on 3/9/17.

 */

//TODO location latlngs are hardcoded

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final double DIST_IN_MILE = 1609;
    private static final float STROKE_WIDTH = 2;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.progress_bar_maps)
    protected ProgressBar mProgressBar;

    @BindView(R.id.frame_layout_maps)
    protected FrameLayout mFrameLayout;

    private GoogleMap mMap;

    private double srcLat = 18.499502;

    private double srcLng = 73.821873;

    private NearbyPlacesAdapter mNearbyPlacesAdapter;

    private RootGooglePlaces rootGooglePlaces;

    private int selectedCardPos = 0;

    private ArrayList<Place> placeArrayList;

    private List<Result> resultArrayList;

    private Place place;

    private BlinkingMarker mBlinkingMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        getSupportActionBar().hide();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        mProgressBar.setVisibility(View.VISIBLE);
        mFrameLayout.setAlpha((float) 0.5);

        nearbyPlaces();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void nearbyPlaces() {

        if (ApiClient.getClient(this) != null) {
            ApiHelper service;
            service = ApiClient.getClient(this).create(ApiHelper.class);
            Call<RootGooglePlaces> call = service.getGooglePlaces(srcLat + "," + srcLng);
            call.enqueue(new Callback<RootGooglePlaces>() {
                @Override
                public void onResponse(Call<RootGooglePlaces> call, final Response<RootGooglePlaces> response) {
                    if (response.isSuccessful()) {

                        mProgressBar.setVisibility(View.GONE);
                        mFrameLayout.setAlpha((float) 1.0);

                        rootGooglePlaces = new RootGooglePlaces();
                        rootGooglePlaces = response.body();
                        resultArrayList = response.body().getResults();
                        for (int i = 0; i < 12; i++) {
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
                        for (int i = 13; i < 20; i++) {
                            Double lat = Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLat());
                            Double lng = Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLng());
                            String placeName = resultArrayList.get(i).getName();

                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng = new LatLng(lat, lng);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rest_marker));
                            markerOptions.position(latLng);
                            markerOptions.title(placeName);
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                        }
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

                    //Place API call
                    if (resultArrayList != null) {
                        if (resultArrayList.size() == 0) {
                        } else {
                            placeArrayList = new ArrayList<>();
                            for (int i = 0; i < (resultArrayList.size() - 1); i++) {
                                Location destination = new Location("Destination");
                                destination.setLatitude
                                        (Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLat()));
                                destination.setLongitude
                                        (Double.parseDouble(resultArrayList.get(i).getGeometry().getLocation().getLng()));

                                if (resultArrayList.get(i) != null && resultArrayList.get(i).getPhotos() != null &&
                                        resultArrayList.get(i).getPhotos().get(0) != null &&
                                        resultArrayList.get(i).getPhotos().get(0).getPhoto_reference() != null) {
                                    place = new Place(null,
                                            0.0, null,
                                            null,
                                            resultArrayList.get(i).getPhotos().get(0).getPhoto_reference(),null);
                                } else {
                                    place = new Place(null,
                                            0.0, null,
                                            null,
                                            null,
                                              null);
                                }
                                placeArrayList.add(place);
                            }
                        }
                    }
                    mNearbyPlacesAdapter = new NearbyPlacesAdapter(MapsActivity.this,
                            selectedCardPos, placeArrayList, response.body().getResults(),
                            response.body(),
                            new NearbyPlacesAdapter.OnCardItemClickListener() {
                                @Override
                                public void onCardClick(int position, Result results) {
                                    selectedCardPos = position;

                                    startActivity(new Intent(MapsActivity.this, DetailsActivity.class));
                                }
                            });

                    final LinearLayoutManager linearLayoutManager
                            = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL,
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

                            int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                            int lastPos = linearLayoutManager.findLastVisibleItemPosition();
                            int middle = Math.abs(lastPos - firstPos) / 2 + firstPos;

                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_disc_marker);
                            mBlinkingMarker = new BlinkingMarker(bitmap, mMap, 10, 2000);
                            mBlinkingMarker.addToMap(new LatLng(Double.parseDouble(response.body().getResults().
                                    get(middle).getGeometry().getLocation().getLat()),
                                    Double.parseDouble(response.body().getResults().get(middle).getGeometry().
                                            getLocation().getLng())));

                            mBlinkingMarker.startBlinking();
                            mNearbyPlacesAdapter.notifyDataSetChanged();
                        }
                    });
                    //scroll one item at a time
                    SnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(mRecyclerView);

                    mRecyclerView.setAdapter(mNearbyPlacesAdapter);
                }

                @Override
                public void onFailure(Call<RootGooglePlaces> call, Throwable t) {
                    mProgressBar.setVisibility(View.GONE);
                    mFrameLayout.setAlpha((float) 1.0);
                    com.durgesh.restaurant.app.constant.RToast.showToast(MapsActivity.this, "failure");
                }
            });
        }
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
                .strokeColor(ContextCompat.getColor(MapsActivity.this, R.color.colorMapCircleStroke))
                .fillColor(ContextCompat.getColor(MapsActivity.this, R.color.colorMapCircle)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14));
    }

    @OnClick(R.id.imgCurLoc)
    public void imgCurLoc(View view) {
        com.durgesh.restaurant.app.constant.RToast.showToast(MapsActivity.this, "change location");
    }

    @OnClick(R.id.imgFilter)
    public void imgFilters(View view) {
        com.durgesh.restaurant.app.constant.RToast.showToast(MapsActivity.this, "filters");
    }
}
