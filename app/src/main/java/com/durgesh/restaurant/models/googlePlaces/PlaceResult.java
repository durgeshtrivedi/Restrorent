package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Snehal Tembare on 10/10/17.

 */

@Getter
@Setter

public class PlaceResult implements Parcelable{
    private Geometry geometry;

    protected PlaceResult(Parcel in) {
        geometry = in.readParcelable(Geometry.class.getClassLoader());
    }

    public static final Creator<PlaceResult> CREATOR = new Creator<PlaceResult>() {
        @Override
        public PlaceResult createFromParcel(Parcel in) {
            return new PlaceResult(in);
        }

        @Override
        public PlaceResult[] newArray(int size) {
            return new PlaceResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(geometry, flags);
    }
}
