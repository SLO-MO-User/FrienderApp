package com.example.frienderapp;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions options;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private String uid;

    private ArrayList<Location> locations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

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

                        Log.i("update", "locations size " + locations.size() + "");
                        CallTheMap();
                    }
                });

        Log.i("update", uid);
        Log.i("update", "locations last size " + locations.size() + "");

        return rootView;
    }

    private void CallTheMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng newCoord = new LatLng(0, 0);
        options = new MarkerOptions();
        for (Location l : locations) {
            newCoord = new LatLng(Double.parseDouble(l.mLatitude), Double.parseDouble(l.mLongitude));
            options.position(newCoord);
            options.title(l.getDate() + l.getTime());
            options.snippet(l.getCity());
            googleMap.addMarker(options);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newCoord));

    }
}
