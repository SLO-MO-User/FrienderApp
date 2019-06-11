package com.example.frienderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter<Messages> {

    MessageAdapter(Context context, ArrayList<Messages> messages) {
        super(context, 0, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.messages_list_item, parent, false);
        }

        Messages m = getItem(position);

        TextView messageTv = listItemView.findViewById(R.id.message_item_text_view);
        messageTv.setText((m != null) ? m.getMessage() : null);

        TextView dateTV = listItemView.findViewById(R.id.message_item_date_text_view);
        dateTV.setText(m != null ? m.getDate() : null);

        TextView userTV = listItemView.findViewById(R.id.message_item_user_text_view);
        userTV.setText(m != null ? m.getUser() : null);

        return listItemView;
    }
}
