package com.durgesh.restaurant.common;

import android.app.Activity;


/**
 * This class encapsulates the necessary boilerplate code for navigating from one activity to another.
 * Created by durgeshtrivedi on 15/12/17.
 */
//@Singleton
public class Router {
    private Activity mActivity;

    /**
     * This method should be called whenever the foreground activity changes, so that the {@link Navigator}
     * always contains the latest activity which the user is interacting with
     */
    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    /**
     * Assuming that the current activity is the CarBrandsListActivity, this method starts the
     * CarBrandsDetailActivity and additionally sends it the display model of the selected CarBrand
     * item from the list.
     *
     * @param listDisplayModel
     */
//    public void navigateToBrandDetail(CarBrandListDisplayModel listDisplayModel) {
//        mActivity.startActivity(CarBrandDetailIntentHelper.getLaunchingIntent(mActivity, listDisplayModel));
//    }
}

