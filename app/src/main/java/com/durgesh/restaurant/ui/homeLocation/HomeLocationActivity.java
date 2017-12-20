package com.durgesh.restaurant.ui.homeLocation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.PlaceDetails;
import com.durgesh.restaurant.models.googlePlaces.Prediction;
import com.durgesh.restaurant.network.ApiClient;
import com.durgesh.restaurant.ui.PlacesAutoCompleteAdapter;
import com.durgesh.restaurant.ui.RecentSearchAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.drawable.ic_menu_search;
import static com.durgesh.restaurant.ui.map.PlaceAPI.PREDICTIONS;

/**
 * Created by Snehal Tembare on 5/10/17.

 */
public class HomeLocationActivity extends AppCompatActivity {

    private static final String TAG = "HomeLocationActivity";
    private static ArrayList<String> sTrendingSearchList;
    @BindView(R.id.edt_detect_my_location)
    protected EditText mCurrentLocation;

    @BindView(R.id.search_location)
    protected AutoCompleteTextView mAutoCompleteTv;

    @BindView(R.id.list_recent_searches)
    protected RecyclerView mRecyclerview;

    private PlacesAutoCompleteAdapter mAdapter;
    private Location mLocation;
    private String mLocationName;
    private com.durgesh.restaurant.network.ApiHelper service;
    private double lat;
    private double lng;
    public static boolean isLocationSelected;
    private Set<String> list;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_location);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        pref = getSharedPreferences("SnapAndroid", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();
        sTrendingSearchList = new ArrayList<>();

        service = ApiClient.getGoogleClient(this).create(com.durgesh.restaurant.network.ApiHelper.class);
        PREDICTIONS.clear();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLocation = bundle.getParcelable("location");
            if (mLocation != null) {
                Geocoder geocoder = new Geocoder(this);

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(mLocation.getLatitude(),
                            mLocation.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    if (addresses.size() != 0) {
                        Log.v("Full address", "" + addresses);
                        Log.v("Locality", "" + addresses.get(0).getSubLocality());

                        if (addresses.get(0).getSubLocality() != null) {
                            Log.v("Location", "" + addresses.get(0).getSubLocality());
                            mLocationName = addresses.get(0).getSubLocality();
                        } else {
                            if (addresses.get(0).getThoroughfare() != null) {
                                Log.v("Location", "" + addresses.get(0).getThoroughfare());
                                mLocationName = addresses.get(0).getThoroughfare();
                            }
                        }
                    }
                }
            }
        }
        mAdapter = new PlacesAutoCompleteAdapter(this, R.layout.item_recent_searches);
        mAutoCompleteTv.setAdapter(mAdapter);
        mAutoCompleteTv.setThreshold(1);

        mAutoCompleteTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
              /*  if (s.length() == 0) {
                    PREDICTIONS.clear();
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             /*   if (s.length() == 0) {
                    PREDICTIONS.clear();
                }
                if (s.length() > 0) {
                    mAutoCompleteTv.setCompoundDrawablesWithIntrinsicBounds(ic_menu_search, 0,
                            R.drawable.ic_input_delete, 0);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
               /* if (s.length() == 0) {
                    PREDICTIONS.clear();
                }*/
                if (s.length() > 0) {
                    mAutoCompleteTv.setCompoundDrawablesWithIntrinsicBounds(ic_menu_search, 0,
                            R.drawable.ic_input_delete, 0);
                }
                if (s.length() == 0) {
                    PREDICTIONS.clear();
                }
            }
        });

        mAutoCompleteTv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mAutoCompleteTv.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {

                        if (event.getRawX() >= (mAutoCompleteTv.getRight() - mAutoCompleteTv.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            mAutoCompleteTv.setText("");
                            PREDICTIONS.clear();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        mAutoCompleteTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                //Adding in sharedPreferences

                sTrendingSearchList.add(description);

                if (sTrendingSearchList != null) {
                    list = new HashSet<>();
                    list.addAll(sTrendingSearchList);
                    editor.putStringSet("searchedList", list);
                    editor.commit();
                  /*  for (String s : sTrendingSearchList) {
                        Log.v("Trending", s);
                    }*/

                  /* list= pref.getStringSet("searchedList",null);
                    sTrendingSearchList.addAll(list);
                    RecentSearchAdapter adapter = new RecentSearchAdapter(getApplicationContext(), sTrendingSearchList);
                    mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    mRecyclerview.setAdapter(adapter);*/
                }
                if (PREDICTIONS.get(position).getPlace_id() != null) {
                    Log.v("Place ID", "" + PREDICTIONS.get(position).getPlace_id());
                    Log.v("description", "" + parent.getItemAtPosition(position));
                    Call<PlaceDetails> call = service.placeDetails(PREDICTIONS.get(position).getPlace_id());
                    call.enqueue(new Callback<PlaceDetails>() {
                        @Override
                        public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    Log.v(TAG, "lat" + response.body().getResult().getGeometry().getLocation().getLat());
                                    Log.v(TAG, "lng" + response.body().getResult().getGeometry().getLocation().getLng());
                                     lat = Double.parseDouble(response.body().getResult().getGeometry().getLocation().getLat());
                                     lng = Double.parseDouble(response.body().getResult().getGeometry().getLocation().getLng());

                                    Intent intent = new Intent();
                                    intent.putExtra("lat", lat);
                                    intent.putExtra("lng", lng);
                                    setResult(Activity.RESULT_OK, intent);
                                    isLocationSelected = true;
                                    finish();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<PlaceDetails> call, Throwable t) {

                        }
                    });

                }
//                PREDICTIONS.clear();
                mAutoCompleteTv.setText(description);
            }
        });
    }

    @OnClick(R.id.img_close)
    public void closeActivity() {
        finish();
    }

    @OnClick(R.id.edt_detect_my_location)
    public void detectCurrentLocation() {
        com.durgesh.restaurant.app.constant.RToast.showToast(this, getString(R.string.location_changed_to) + " " + mLocationName);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAutoCompleteTv.setText("");

        if (pref != null) {
            list = pref.getStringSet("searchedList", null);
            if (list != null) {
                sTrendingSearchList.addAll(list);
            }
            RecentSearchAdapter adapter = new RecentSearchAdapter(getApplicationContext(), sTrendingSearchList, new OnItemClickListener() {
                @Override
                public void onItemClick(String s) {
//                    mAutoCompleteTv.setText(s);
//                    finish();
                    Intent intent = new Intent();
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                    setResult(Activity.RESULT_OK, intent);
                    isLocationSelected = true;
                    finish();

                }
            });
            mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            mRecyclerview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if (PREDICTIONS != null) {
           /* if (mAutoCompleteTv.getText().length() == 0) {
                PREDICTIONS.clear();
            }*/
            for (Prediction p : PREDICTIONS) {
                Log.v(TAG, "" + p.getDescription());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String s);
    }
}
