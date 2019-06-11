package com.example.frienderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationAdapter extends ArrayAdapter<LocationClass> {
    private int mColorResourceID;

    LocationAdapter(Context context, ArrayList<LocationClass> locations, int color) {
        super(context, 0, locations);
        mColorResourceID = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.location_list_item, parent, false);
        }

        LocationClass l = getItem(position);

        TextView longitudeTV = listItemView.findViewById(R.id.longitude_text_view);
        longitudeTV.setText((l != null) ? l.getLongitude() : null);

        TextView latitudeTV = listItemView.findViewById(R.id.latitude_text_view);
        latitudeTV.setText(l != null ? l.getLatitude() : null);

        TextView timeTV = listItemView.findViewById(R.id.time_text_view);
        timeTV.setText((l != null) ? l.getTime() : null);

        TextView dateTV = listItemView.findViewById(R.id.date_text_view);
        dateTV.setText(l != null ? l.getDate() : null);

        TextView cityTV = listItemView.findViewById(R.id.city_text_view);
        cityTV.setText(l != null ? l.getCity() : null);

        View textContainer = listItemView.findViewById(R.id.text_container_location);
        textContainer.setBackgroundResource(mColorResourceID);

        return listItemView;
    }
}