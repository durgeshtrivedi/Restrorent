package com.durgesh.restaurant.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.durgesh.restaurant.models.googlePlaces.Prediction;
import com.durgesh.restaurant.ui.map.PlaceAPI;

import java.util.ArrayList;

import static com.durgesh.restaurant.ui.map.PlaceAPI.PREDICTIONS;

/**
 * Created by Snehal Tembare on 26/9/17.

 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private static final String TAG = "PlaceAdapter";
//    public static  ArrayList<String> resultList=new ArrayList<>();
    public static  ArrayList<Prediction> predictions=new ArrayList<>();

    Context mContext;
    int mResource;
    LayoutInflater mInflater;

    public PlaceAPI mPlaceAPI;

    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);

        mContext = context;
        mResource = resource;
        mPlaceAPI = new PlaceAPI(mContext);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // Last item will be the footer
//        return resultList.size();
        return PREDICTIONS.size();
    }

    @Override
    public String getItem(int position) {
//        return resultList.get(position);
        String s=PREDICTIONS.get(position).getDescription();
        Log.v(TAG,""+s);
        return PREDICTIONS.get(position).getDescription();
    }

   /* @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_recent_searches, null);
            holder = new ViewHolder();
            holder.textView = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.textView.setText(PREDICTIONS.get(position).getDescription());
        return convertView;
    }
    private class ViewHolder {
        public TextView textView;
    }*/

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
//                    resultList = mPlaceAPI.autocomplete(constraint.toString());
//                    PREDICTIONS =
                            mPlaceAPI.autocomplete(constraint.toString());

                    ArrayList<String> arrayList=new ArrayList<>();
                    for (Prediction p:PREDICTIONS){
//                        Log.v("performFiltering",""+p.getDescription());
                        arrayList.add(p.getDescription());
                    }

                    filterResults.values = arrayList;
                    filterResults.count = arrayList.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }


}