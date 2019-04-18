package com.example.frienderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FriendsAdapter extends ArrayAdapter<Friend> {

    private int mColorResourceID;

    FriendsAdapter(Context context, ArrayList<Friend> friends, int color) {
        super(context, 0, friends);
        mColorResourceID = color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.friends_list_item, parent, false);
        }

        Friend f = getItem(position);

        TextView nameTV = listItemView.findViewById(R.id.friend_nickname_text_view);
        nameTV.setText((f != null) ? f.getNickname() : null);

        TextView emailTV = listItemView.findViewById(R.id.friend_email_text_view);
        emailTV.setText(f != null ? f.getEmail() : null);

        ImageView profileIV = listItemView.findViewById(R.id.profile_image_view);
        //Log.i("update", String.format("inside adapter images uris %s\n", f != null ? f.getProfileImageURI() : null));
        String internetUrl = f != null ? f.getProfileImageURI() : null;
        Glide
                .with(listItemView)
                .load(internetUrl)
                .into(profileIV);

        View textContainer = listItemView.findViewById(R.id.text_container_friends);
        textContainer.setBackgroundResource(mColorResourceID);

        return listItemView;
    }
}
