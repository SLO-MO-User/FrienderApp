package com.example.frienderapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private static final String USERUID = "USERUID";
    private static final String DATE = "DATE";
    private static final String LOCATION = "LOCATION";

    private FirebaseFirestore db;
    private String uid;
    private String s = "";
    List<String> list1;
    private ArrayList<Friend> friends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();

        db.collection("Friendships").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    list1 = new ArrayList<>();
                    if (task.getResult().getData() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            list1.add(entry.getKey());
                            Log.i("update", entry.getKey());
                        }
                    }
                    ListFriends(rootView, list1);
                    Log.i("update", "after ListFriends");
                }
            }
        });

        return rootView;
        //return textView;
    }

    private void setFriendsAdapter(View rootView) {
        FriendsAdapter adapter = new FriendsAdapter(getActivity(), friends, R.color.category_friends);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getActivity(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }

    private void ListFriends(final View rootView, final List<String> list2) {
        friends = new ArrayList<>();
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("update", "List friends task success");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (list2.contains(document.getId())) {
                            //Log.i("update", "friend name : " + document.getString("NAME"));
                            friends.add(new Friend(document.getString("NICKNAME"), document.getString("EMAIL"), document.getString("PROFILE_IMAGE")));
                        }
                    }
                    Log.i("update", "friends size : " + friends.size());
                    setFriendsAdapter(rootView);
                }
            }
        });
    }

}
