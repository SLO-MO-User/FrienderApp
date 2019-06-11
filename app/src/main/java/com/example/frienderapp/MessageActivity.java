package com.example.frienderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    EditText messageTV;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String friendUID;
    private String uid;
    private ArrayList<Messages> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            friendUID = b.getString("fuid");
            Log.i("update", "messaged friend uid : " + friendUID);
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        messages = new ArrayList<>();

        messageTV = findViewById(R.id.message_edit_text);

        getMessages(new Messages("x", "x", "x"));

        setMessageAdapter(messages);
    }

    private void getMessages(final Messages newMessage) {
        final ArrayList<Messages> messages2 = new ArrayList<>();
        db.collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("update", "task success");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Object time = document.get("date");
                                Timestamp t = (Timestamp) time;
                                Date d = t.toDate();
                                if ((document.getString("from").equals(uid) && document.getString("to").equals(friendUID))
                                        || (document.getString("from").equals(friendUID) && document.getString("to").equals(uid)))
                                    messages2.add(new Messages(document.getString("message"), d.toString(), document.getString("sender")));
                            }

                            if (!(newMessage.getUser().equals("x") && newMessage.getDate().equals("x"))) {
                                messages2.add(newMessage);
                            }
                            Collections.sort(messages2);

                            Log.i("update", "messages exist? : " + messages2.isEmpty() + " count : " + messages2.size());
                            setMessageAdapter(messages2);
                        } else {
                            Log.d("update", "Error getting documents: ", task.getException());
                            Log.i("update", "task failure");
                        }
                    }
                });
    }

    private void setMessageAdapter(ArrayList<Messages> messagess) {
        MessageAdapter adapter = new MessageAdapter(this, messagess);
        final ListView listView = findViewById(R.id.messages_list_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public void SendMessage(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageTV.getText().toString().isEmpty()) {
                    Messages messageToSend = new Messages(messageTV.getText().toString(), new Date().toString(), currentUser.getDisplayName());

                    Map<String, Object> newMessage = new HashMap<>();
                    if (friendUID.compareTo(uid) > 0) {
                        newMessage.put("from", uid);
                        newMessage.put("to", friendUID);
                        newMessage.put("date", Timestamp.now());
                        newMessage.put("message", messageTV.getText().toString());
                        newMessage.put("sender", currentUser.getDisplayName());
                    }
                    if (friendUID.compareTo(uid) < 0) {
                        newMessage.put("from", uid);
                        newMessage.put("to", friendUID);
                        newMessage.put("date", Timestamp.now());
                        newMessage.put("message", messageTV.getText().toString());
                        newMessage.put("sender", currentUser.getDisplayName());
                    }
                    CollectionReference locations = db.collection("Messages");
                    locations.document().set(newMessage);
                    Log.i("update", "message uploaded");
                    messageTV.setText("");
                    getMessages(messageToSend);
                }
            }
        });
    }
}
