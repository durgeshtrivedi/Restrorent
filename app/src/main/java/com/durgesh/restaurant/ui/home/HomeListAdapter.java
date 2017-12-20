package com.durgesh.restaurant.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.durgesh.restaurant.R;
import com.durgesh.restaurant.models.googlePlaces.Place;
import com.durgesh.restaurant.network.ApiClient;
import com.durgesh.restaurant.network.ApiHelper;
import com.durgesh.restaurant.ui.homeLocation.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Snehal Tembare on 30/8/17.

 */

public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {
    private Context mContext;
     private List<Place> placeArrayList;
    private OnItemClickListener onItemClickListener;
    private ApiHelper service;

    public HomeListAdapter(Context context, List<Place> placeArrayList,
                           OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.placeArrayList = placeArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_home,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        service = ApiClient.getGoogleClient(mContext).create(com.durgesh.restaurant.network.ApiHelper.class);

        Place place=placeArrayList.get(position);
        holder.mTxtRestName.setText(place.getName());
        holder.mTxtRatingCount.setText(String.valueOf(place.getRating()));

        if (place.getOpeningHours()!=null && place.getOpeningHours().isOpen_now()){
            holder.mLayoutHotelStatus.setVisibility(View.VISIBLE);
            holder.mLayoutParet.setAlpha((float) 0.5);
        }else {
            holder.mLayoutHotelStatus.setVisibility(View.GONE);
            holder.mLayoutParet.setAlpha(1);
        }
        float mile = (place.getDistance())/1609.34f;
        holder.mTxtDistance.setText(String.format("%.2f",mile)+"mile");

        Picasso.with(mContext).load(place.getPhoto()).into(holder.mImgRestaurant);

        final Call<ResponseBody> call1 = service.searchPhotos(place.getPhoto());
        Log.v("callPlaceApi",""+place.getPhoto());
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (response.body() != null) {
                        Log.v("Photo link", "" + response.body().byteStream());
                        int targetWidth=356;
                        Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());
                        double aspectRatio = (double) bm.getHeight() / (double) bm.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);

                        holder.mImgRestaurant.setImageBitmap(Bitmap.createScaledBitmap(bm, targetWidth, targetHeight, false));

                    }else {
                        holder.mImgRestaurant.setImageResource(R.drawable.ic_hotel);
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        //TODO:Restaurant type need to fix
//        holder.mTxtRestType.setText();

        holder.bind(place,onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgRestaurant;
        private TextView mTxtRestName;
        private TextView mTxtRatingCount;
        private TextView mTxtDistance;
        private TextView mTxtRestType;
        private LinearLayout mLayoutHotelStatus;
        private RelativeLayout mLayoutParet;


        public ViewHolder(View itemView) {
            super(itemView);
            mImgRestaurant= (ImageView) itemView.findViewById(R.id.img_restaurant);
            mTxtRestName= (TextView) itemView.findViewById(R.id.txt_rest_name);
            mTxtRatingCount= (TextView) itemView.findViewById(R.id.txt_rating_count);
            mTxtDistance= (TextView) itemView.findViewById(R.id.txt_distance);
            mTxtRestType= (TextView) itemView.findViewById(R.id.txt_type);
            mLayoutHotelStatus= (LinearLayout) itemView.findViewById(R.id.layouthotelstatus);
            mLayoutParet= (RelativeLayout) itemView.findViewById(R.id.parent_layout);
        }

        public void bind(final Place place, final OnItemClickListener onItemClickListener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(place);
                }
            });
        }
    }
}
