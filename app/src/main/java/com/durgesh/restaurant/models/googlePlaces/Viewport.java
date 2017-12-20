package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;
import com.durgesh.restaurant.models.googleDir.Northeast;
import com.durgesh.restaurant.models.googleDir.Southwest;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Prajakta Patil on 4/9/17.
 * Copyright © 2017 Synerzip. All rights reserved
 */
@Getter
@Setter
public class Viewport implements Parcelable {

    public static final Creator<Viewport> CREATOR = new Creator<Viewport>() {
        @Override
        public Viewport createFromParcel(Parcel source) {
            return new Viewport(source);
        }

        @Override
        public Viewport[] newArray(int size) {
            return new Viewport[size];
        }
    };
    private Southwest southwest;
    private Northeast northeast;

    public Viewport() {
    }

    protected Viewport(Parcel in) {
        this.southwest = in.readParcelable(Southwest.class.getClassLoader());
        this.northeast = in.readParcelable(Northeast.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.southwest, flags);
        dest.writeParcelable(this.northeast, flags);
    }
}
