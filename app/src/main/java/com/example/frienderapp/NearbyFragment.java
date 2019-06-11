package com.example.frienderapp;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {

    private static final String USERUID = "USERUID";
    private static final String DATE = "DATE";
    private static final String LOCATION = "LOCATION";
    LocationManager locationManager;
    private FirebaseFirestore db;
    private String uid;
    private ArrayList<Friend> allUsers;
    private ArrayList<Friend> nearbyUsers;
    private Set<String> nearbyUsersUIDList;
    private ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        uid = currentUser.getUid();

        nearbyUsers = new ArrayList<>();
        allUsers = new ArrayList<>();
        nearbyUsersUIDList = new TreeSet<>();

        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i("update", "permission error");
            }
        /*
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1 * 60 * 1000, 10, locationListenerGPS);
                */

        db.collection("Locations")
                .orderBy("DATE", Query.Direction.DESCENDING)
                .limit(25)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("update", "task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getString("USERUID").equals(uid)) {
                                    //Log.d("update", document.getId() + " => " + document.getData());
                                    Object loc = document.get("LOCATION");
                                    Object time = document.get("DATE");
                                    Timestamp t = (Timestamp) time;
                                    GeoPoint g = (GeoPoint) loc;

                                    Date d = t.toDate();
                                    Date currentTime = Calendar.getInstance().getTime();

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                    String documentDate = format.format(d.getTime());
                                    String currentDate = format.format(currentTime.getTime());

                                    //check if its same day and last hour
                                    int documentHour = Integer.parseInt(documentDate.substring(11, 13));
                                    int currentHour = Integer.parseInt(currentDate.substring(11, 13));
                                    if (documentDate.substring(0, 10).equals(currentDate.substring(0, 10)) && documentHour >= (currentHour - 1)) {
                                        nearbyUsersUIDList.add(document.getString("USERUID"));
                                    }
                                }
                            }
                            ListNearbyUsers(rootView, nearbyUsersUIDList);
                        } else {
                            Log.d("update", "Error getting documents: ", task.getException());
                            Log.i("update", "task failure");
                        }

                        Log.i("update", "all locations users size " + nearbyUsersUIDList.size() + "");
                    }
                });


        return rootView;
    }


    private void setNearbyUsersAdapter(View rootView, final ArrayList<Friend> users) {
        FriendsAdapter adapter = new FriendsAdapter(getActivity(), users, R.color.category_friends);
        final ListView listView = rootView.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Friend f = users.get(position);
                Log.i("update", "pupup id : " + f.getUID());

                PopupMenu popup = new PopupMenu(getActivity(), view);
                popup.getMenuInflater().inflate(R.menu.popup_menu_nearby, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.befriend_item:
                                FriendPopupOption(f);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }

    private void FriendPopupOption(Friend friend) {
        Map<String, Object> newFriendship = new HashMap<>();
        newFriendship.put(friend.getUID(), true);

        CollectionReference locations = db.collection("Friendships");
        locations.document(uid).set(newFriendship, SetOptions.merge());

        Log.i("update", "friend added");
    }

    private void ListNearbyUsers(final View rootView, final Set<String> nearbylist) {

        final List<String> friendList = new ArrayList<>();
        db.collection("Friendships").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getData() != null) {
                        Map<String, Object> map = task.getResult().getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            friendList.add(entry.getKey());
                            Log.i("update", entry.getKey());
                        }
                    }
                }
            }
        });


        Log.i("update", "all locations users final nearby size " + nearbylist.size() + "");

        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.i("update", "List nearby users task success");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (nearbylist.contains(document.getId()) && !friendList.contains(document.getId())) {
                            //if (!document.getId().equals(uid)) {
                            //Log.i("update", "friend name : " + document.getString("NAME"));
                            nearbyUsers.add(new Friend(document.getString("NICKNAME"), document.getString("EMAIL"),
                                    document.getString("PROFILE_IMAGE"), document.getId()));
                            //}
                        }
                    }
                    //Log.i("update", "friends size : " + friends.size());
                    setNearbyUsersAdapter(rootView, nearbyUsers);
                }
            }
        });
    }
}


/////////////////////////



/*
    private Set<String> GetAllNearbyUsers() {
        Date date = new Date(System.currentTimeMillis() - (1 * 60 * 60 * 1000));

        db.collection("Locations")
                .orderBy("DATE", Query.Direction.DESCENDING)
                .limit(25)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("update", "task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getString("USERUD").equals(uid)) {
                                    //Log.d("update", document.getId() + " => " + document.getData());
                                    Object loc = document.get("LOCATION");
                                    Object time = document.get("DATE");
                                    Timestamp t = (Timestamp) time;
                                    GeoPoint g = (GeoPoint) loc;

                                    Date d = t.toDate();
                                    Date currentTime = Calendar.getInstance().getTime();

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
                                    String documentDate = format.format(d.getTime());
                                    String currentDate = format.format(currentTime.getTime());


                                    //check if its same day and last hour
                                    int documentHour = Integer.parseInt(documentDate.substring(11, 13));
                                    int currentHour = Integer.parseInt(currentDate.substring(11, 13));
                                    if (documentDate.substring(0, 10).equals(currentDate.substring(0, 10)) && documentHour >= (currentHour - 1)) {
                                        nearbyUsersUIDList.add(document.getString("USERUID"));
                                    }
                                }
                            }
                        } else {
                            Log.d("update", "Error getting documents: ", task.getException());
                            Log.i("update", "task failure");
                        }

                        Log.i("update", "all locations users size " + nearbyUsersUIDList.size() + "");
                    }
                });
        return nearbyUsersUIDList;
    }
/*
    private boolean getDistance(double lat, double lon) {
        final GeoPoint[] g = new GeoPoint[1];
        float[] results;
        results = new float[3];

        db.collection("Locations")
                .orderBy("DATE", Query.Direction.DESCENDING)
                .whereEqualTo("USERUID", uid)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Object loc = document.get("LOCATION");
                                g[0] = (GeoPoint) loc;
                            }
                        }
                    }
                });

        double currentLatitude = g[0].getLatitude();
        double currentLongitude = g[0].getLongitude();


        if (lat != 0.0 || lon != 0.0) {
            Location.distanceBetween(
                    currentLatitude,
                    currentLongitude,
                    lat,
                    lon,
                    results);
        }
        //Log.i("update", "current lat: " + currentLatitude + "  current long : " + currentLongitude + "\n target lat:" + lat + "  target lon:" + lon + "  nearby results current location distance to target : " + results[0] / 1000);
        if ((results[0] / 1000) <= 5) {
            return true;
        }
        return false;
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(final Location location) {
            Log.i("update", "inside on location changed" + location.toString());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i("update", "inside on status changed" + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i("update", "inside on provider enabled" + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i("update", "inside on provider disabled" + s);
        }
    };


/*
    private void setNearbyUsersAdapter(ArrayList<Friend> users) {
        Log.i("update", "inside setfriendsadapter");
        FriendsAdapter adapter = new FriendsAdapter(getActivity(), users, R.color.category_friends);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(getActivity(), view);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_nearby, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getActivity(), "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }

    private void getNearbyUsersFromFB() {
        final long val = 3600;
        final long currentTime = Timestamp.now().getSeconds();

        Log.i("update", "getFB");


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
                                if (!document.getString("USERUID").equals(uid)) {
                                    Log.i("update", "userid found : " + document.getString("USERUID"));
                                    Object loc = document.get("LOCATION");
                                    Object time = document.get("DATE");
                                    Object userID = document.get("USERUID");

                                    GeoPoint g = (GeoPoint) loc;
                                    Timestamp t = (Timestamp) time;
                                    String s = (String) userID;

                                    if (getDistance(g.getLatitude(), g.getLongitude())) {
                                        if ((currentTime - t.getSeconds()) <= val + 8000000) {
                                            Log.i("update", "yeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeet");
                                            Log.i("update", "\nValue of document uid string is : " + s
                                                    + "\ncurrentTime in seconds" + currentTime
                                                    + "\nDocument time in seconds" + t.getSeconds()
                                                    + "\nDocument time in seconds plus val" + (t.getSeconds() + val)
                                                    + "\ntime diff is : " + (currentTime - (t.getSeconds() + val)));
                                            ListNearbyUsers(s);
                                        }
                                    }
                                }
                                else Log.i("update", "userid not found found : " + document.getString("USERUID"));
                            }
                        }
                    }
                });
    }

    private void ListNearbyUsers(final String userID) {
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Log.i("update", "List friends task success");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        nearbyUsers.add(new Friend(document.getString("NICKNAME"), document.getString("EMAIL"), document.getString("PROFILE_IMAGE")));
                    }
                    Log.i("update", "friends size : " + nearbyUsers.size());
                    setNearbyUsersAdapter(nearbyUsers);
                }
            }
        });
    }

    private boolean getDistance(double lat, double lon) {
        float[] results;
        results = new float[3];
        Log.i("update", "inside nearby getDistance()");
        Log.i("update", "currents location : " + lat + "," + lon);

        if (lat != 0.0 || lon != 0.0) {
            locationManager.removeUpdates(locationListenerGPS);
            Location.distanceBetween(
                    currentLatitude,
                    currentLatitude,
                    lat,
                    lon,
                    results);
        }
        //Log.i("update", "current lat: " + currentLatitude + "  current long : " + currentLongitude + "\n target lat:" + lat + "  target lon:" + lon + "  nearby results current location distance to target : " + results[0] / 1000);
        if ((results[0] / 1000) <= 2000) {
            return true;
        }
        return false;
    }
*/
