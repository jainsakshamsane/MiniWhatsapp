package com.miniwhatsapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miniwhatsapp.Adapters.UserAdapter;
import com.miniwhatsapp.Models.MessageModel;
import com.miniwhatsapp.Models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat_Section extends Fragment {

    UserAdapter adapter;
    List<UserModel> userlist = new ArrayList<>();
    RecyclerView recyclerView;

    List<MessageModel> messageList = new ArrayList<>();
    List<MessageModel> newmessage = new ArrayList<>();
    String userId;
    // Declare a variable to store the latest timestamp
    String latestTimestamp = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_section, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use getContext() instead of 'this'
        recyclerView.setHasFixedSize(true);

        SharedPreferences sharedPreferencesss = getContext().getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");
        // Get the Firebase database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        // Navigate to the "messages" node
        DatabaseReference messagesRef = databaseReference.child("message");

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        adapter = new UserAdapter(userlist, messageList, getContext()); // Initialize the adapter with userlist

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String namefirst = ds.child("firstname").getValue(String.class);
                    String namelast = ds.child("lastname").getValue(String.class);
                    String imageurl = ds.child("imageurl").getValue(String.class);
                    String userid = ds.child("userid").getValue(String.class);
                    //String email = ds.child("email").getValue(String.class);
                    String timestamp = ds.child("timestamp").getValue(String.class);

                    // Check if the user is not the currently logged-in user
                    if (!userid.equals(userId) && !imageurl.equals("")) {
                        UserModel user = new UserModel(namefirst, namelast, imageurl, timestamp,userid);
                        userlist.add(user);
                    }
                }

                // Set the adapter after all data is retrieved
                if(!userlist.isEmpty()){
                    recyclerView.setAdapter(adapter);
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                messagesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messageList.clear();
                        for (UserModel users : userlist) {
                                Date date1 = null;
                                Date date2 = null;
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            // Get the last child node (latest)
                            DataSnapshot latestChildSnapshot = null;
                            // Iterate through each child of the current messageSnapshot
                            for (DataSnapshot childSnapshot : messageSnapshot.getChildren()) {
                                latestChildSnapshot = childSnapshot;
                            }

                            // Access the data under the latest child node
                            if (latestChildSnapshot != null) {
                                MessageModel chat = latestChildSnapshot.getValue(MessageModel.class);

                                // Access the data under each child node
                                chat.setMessageText(latestChildSnapshot.child("messageText").getValue(String.class));
                                chat.setReceiverUserId(latestChildSnapshot.child("receiverUserId").getValue(String.class));
                                chat.setSenderUserId(latestChildSnapshot.child("senderUserId").getValue(String.class));
                                chat.setTimestamp(latestChildSnapshot.child("timestamp").getValue(String.class));

                                if (((userId.equals(chat.getSenderUserId()) && (users.getUserid().equals(chat.getReceiverUserId()))))) {
                                    try {
                                        date1 = dateFormat.parse(chat.getTimestamp());
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                if(((userId.equals(chat.getReceiverUserId())) && (users.getUserid().equals(chat.getSenderUserId())))) {
                                    try {
                                        date2 = dateFormat.parse(chat.getTimestamp());
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                if(date1 != null && date2 != null){
                                    if (date1.compareTo(date2) > 0) {
                                        messageList.add(chat);
                                    } else if (date1.compareTo(date2) < 0) {
                                        messageList.add(chat);
                                    }
                                }

                                // Get the latest timestamp
                                latestTimestamp = chat.getTimestamp();
                            }
                        }
                    }

                        //isko last me
                        // Update the adapter after fetching all chats
                        adapter.notifyDataSetChanged();

                        // Pass the latest timestamp to the adapter
                        adapter.setLatestTimestamp(latestTimestamp);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                        Log.e("Chat_Fragment", "Error retrieving messages: " + databaseError.getMessage());
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve users", Toast.LENGTH_SHORT).show();
            }
        });




//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        for (UserModel users : userlist) {
//            Date date1 = null;
//            Date date2 = null;
//            int i1=0,i2=0;
//            for (int i = 0; i < messageList.size(); i++) {
//                MessageModel messages = messageList.get(i);
//                if(messages.getSenderUserId().equals(userId) && messages.getReceiverUserId().equals(users.getUserid())){
//                    try {
//                        date1 = dateFormat.parse(messages.getTimestamp());
//                        i1 = i;
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                if(messages.getSenderUserId().equals(users.getUserid()) && messages.getReceiverUserId().equals(userId)){
//                    try {
//                        date2 = dateFormat.parse(messages.getTimestamp());
//                        i2 = i;
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//            if(date1 != null && date2 != null){
//                if (date1.compareTo(date2) > 0) {
//                    messageList.remove(i1);
//                } else if (date1.compareTo(date2) < 0) {
//                    messageList.remove(i2);
//                }
//            }
//
//        }

        adapter.notifyDataSetChanged();
        return view;
    }
}
