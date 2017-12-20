package com.durgesh.restaurant.models.googlePlaces;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Snehal Tembare on 31/8/17.

 */

@Setter
@Getter

public class OpeningHours implements Parcelable{
    private boolean open_now;
    private ArrayList<String> weekday_text;

    protected OpeningHours(Parcel in) {
        open_now = in.readByte() != 0;
        weekday_text = in.createStringArrayList();
    }
    public OpeningHours(boolean open_now,ArrayList<String> weekday_text) {
        this.open_now = open_now;
       this.weekday_text = weekday_text;
    }

    public static final Creator<OpeningHours> CREATOR = new Creator<OpeningHours>() {
        @Override
        public OpeningHours createFromParcel(Parcel in) {
            return new OpeningHours(in);
        }

        @Override
        public OpeningHours[] newArray(int size) {
            return new OpeningHours[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (open_now ? 1 : 0));
        dest.writeStringList(weekday_text);
    }
}
