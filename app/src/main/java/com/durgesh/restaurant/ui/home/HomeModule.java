package com.durgesh.restaurant.ui.home;

import com.durgesh.restaurant.dagger.FragmentScoped;
import com.durgesh.restaurant.ui.home.fragments.CouponsFragment;
import com.durgesh.restaurant.ui.home.fragments.DealsFragment;
import com.durgesh.restaurant.ui.home.fragments.HomeListFragment;
import com.durgesh.restaurant.ui.home.fragments.MapFragment;
import com.durgesh.restaurant.ui.home.fragments.ProfileFragment;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by durgeshtrivedi on 15/12/17.
 */

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link HomePresenter}.
 */
@Module
public abstract class HomeModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract CouponsFragment couponsFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DealsFragment dealsFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract HomeListFragment homeListFragment();


    @FragmentScoped
    @ContributesAndroidInjector
    abstract MapFragment mapFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment profileFragment();

    abstract HomeInteractor getHomeInteracter();

   // abstract MapInteractor getMapInteracter();

    @Provides
    static MapInteractor provideMapIntracter() {return new MapInteractor();}

    @Provides
    static HomeContract.Presenter provideHomePresenter(HomeInteractor interactor) {
        HomeContract.Presenter  presenter = new HomePresenter(interactor);
        interactor.setPresenter(presenter);
        return presenter;
    }

    @Provides
    static HomeContract.MapPresenter provideHomeMapPresenter(MapInteractor interactor) {
        HomeContract.MapPresenter  presenter = new HomeMapPresenter(interactor);
        interactor.setMapPresenter(presenter);
        return presenter;
    }

    /// Test Code for reference
//    @ActivityScoped
//    @Binds
//    abstract HomeContract.Presenter homePresenter(HomePresenter presenter);
}