package com.durgesh.restaurant.network;

import com.durgesh.restaurant.models.googlePlaces.Hotel;
import com.durgesh.restaurant.models.googlePlaces.ImageCompareResponse;
import com.durgesh.restaurant.models.googlePlaces.PlaceDetails;
import com.durgesh.restaurant.models.googlePlaces.PlacesAutoCompleteData;
import com.durgesh.restaurant.models.googleDir.RootGoogleDir;
import com.durgesh.restaurant.models.googlePlaces.RootGooglePlaces;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Prajakta Patil on 30/8/17.

 */

public interface ApiHelper {
    /**
     * google directions api
     *
     * @param source
     * @param dest
     * @return
     */
    @GET(com.durgesh.restaurant.app.constant.RWebConstants.SX_GOOGLE_DIR_API)
    Call<RootGoogleDir> getGoogleDir(@Query("origin") String source,
                                     @Query("destination") String dest);

    /**
     * google places api
     *
     * @param source
     * @return
     */
    @GET(com.durgesh.restaurant.app.constant.RWebConstants.SX_GOOGLE_PLACES_API)
    Call<RootGooglePlaces> getGooglePlaces(@Query("location") String source);

    /**
     * get place photos
     *
     * @param photoreference
     * @return
     */
    @GET(com.durgesh.restaurant.app.constant.RWebConstants.SEARCH_PHOTOS)
    Call<ResponseBody> searchPhotos(@Query("photoreference") String photoreference);


    @GET(com.durgesh.restaurant.app.constant.RWebConstants.SEARCH_PLACES)
    Call<Hotel> searchNearestPlaces(@Query("location") String location);


    @GET(com.durgesh.restaurant.app.constant.RWebConstants.SEARCH_PREDICTIONS)
    Call<PlacesAutoCompleteData> searchPredictions(@Query("input") String photoreference);

    @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
    @POST(com.durgesh.restaurant.app.constant.RWebConstants.UPLOAD_IMAGE)
    Call<ImageCompareResponse> uploadImage(@Body RequestBody key);


    @GET(com.durgesh.restaurant.app.constant.RWebConstants.PLACE_DETAILS)
    Call<PlaceDetails> placeDetails(@Query("placeid") String placeid);
}