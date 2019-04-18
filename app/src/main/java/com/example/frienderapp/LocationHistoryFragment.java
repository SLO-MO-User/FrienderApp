package com.example.frienderapp;


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationHistoryFragment extends Fragment {

    private static final String USERUID = "USERUID";
    private static final String DATE = "DATE";
    private static final String LOCATION = "LOCATION";

    private FirebaseFirestore db;
    private String uid;

    private ArrayList<Location> locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();

        Log.i("update", "before call");

        locations = new ArrayList<>();
        db.collection("Locations")
                .orderBy("DATE", Query.Direction.DESCENDING)
                .whereEqualTo("USERUID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("update", "task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("update", document.getId() + " => " + document.getData());
                                Object loc = document.get("LOCATION");
                                Object time = document.get("DATE");
                                Timestamp t = (Timestamp) time;

                                Date d = t.toDate();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                String s = format.format(d.getTime());

                                GeoPoint g = (GeoPoint) loc;
                                //String stateName = "";
                                String cityName = "";
                                //String countryName = "";

                                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(g.getLatitude(), g.getLongitude(), 1);
                                    cityName = addresses.get(0).getAddressLine(0);
                                    //stateName = addresses.get(0).getAddressLine(1);
                                    //countryName = addresses.get(0).getAddressLine(2);
                                    //Log.i("update",cityName + " " + stateName + " " + countryName);
                                } catch (IOException e) {
                                    Log.i("update", e.toString());
                                }

                                //Log.i("update", "JUST LOCATION : " + g.getLatitude() + ", " + g.getLongitude() + " time: " + format.format(d.getTime()) );
                                locations.add(new Location(g.getLongitude() + "", g.getLatitude() + "", s.substring(0, 10), s.substring(10), cityName));
                            }
                        } else {
                            Log.d("update", "Error getting documents: ", task.getException());
                            Log.i("update", "task failure");
                        }
                        setLocationAdapter(rootView);
                    }
                });

        Log.i("update", uid);
        Log.i("update", "after size " + locations.size() + "");

        return rootView;
    }

    private void setLocationAdapter(View rootView) {
        LocationAdapter adapter = new LocationAdapter(getActivity(), locations, R.color.category_location);
        ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                Location l = locations.get(position);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + l.getLatitude() + "," + l.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                */

                Bundle extra = new Bundle();
                extra.putSerializable("objects", locations);

                if (locations != null) {
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    intent.putExtra("extra", extra);
                    startActivity(intent);
                }


            }
        });
    }

}
