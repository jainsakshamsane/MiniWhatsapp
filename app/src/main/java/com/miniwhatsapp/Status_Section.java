package com.miniwhatsapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miniwhatsapp.Adapters.StatusAdapter;
import com.miniwhatsapp.Adapters.UserAdapter;
import com.miniwhatsapp.Models.StatusModel;
import com.miniwhatsapp.Models.UserModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Status_Section extends Fragment {

    ImageView profileimage, statusimage, mystatusimage;
    String userId, imageurls, urlofimage, imageurl;
    TextView statusText, mycaption, name, time;
    LinearLayout linear01, linear3;
    List<StatusModel> statusModelList = new ArrayList<>();
    RecyclerView recyclerView;
    StatusAdapter statusAdapter;

    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.status_section, container, false);

        profileimage = view.findViewById(R.id.profileimage);
        statusimage = view.findViewById(R.id.statusimage);
        statusText = view.findViewById(R.id.editprofile);
        mystatusimage = view.findViewById(R.id.mystatusimage);
        mycaption = view.findViewById(R.id.mycaption);
        linear01 = view.findViewById(R.id.linear01);
        linear3 = view.findViewById(R.id.linear3);
        name = view.findViewById(R.id.name);
        time = view.findViewById(R.id.time);

        sharedPreferences = getContext().getSharedPreferences("user_details", MODE_PRIVATE);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use getContext() instead of 'this'
        recyclerView.setHasFixedSize(true);

        SharedPreferences sharedPreferencesss = getContext().getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    imageurl = ds.child("imageurl").getValue(String.class);

                        Picasso.get().load(imageurl).into(profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listener on profileimage
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UploadStatus.class);
                startActivity(intent);
            }
        });

        DatabaseReference statusref = FirebaseDatabase.getInstance().getReference("status");

        statusref.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    imageurls = ds.child("imageurl").getValue(String.class);
                    String captions = ds.child("text").getValue(String.class);
                    String usersid = ds.child("userid").getValue(String.class);
                    String timeofstamp = ds.child("timestamp").getValue(String.class);
                    String statusname = "My Status";

                    // Step 1: Parse timestamp into a Date object
                    Date date = parseTimestamp(timeofstamp);

                    // Step 2: Get current date and time
                    Date currentDate = new Date();

                    // Step 3: Compare dates to determine if it's today, yesterday, or another day
                    String dateString = getDateString(date, currentDate);

                    // Step 4: Format time into 12-hour format with AM/PM
                    String timeString = getTimeString(date);

                    if (!usersid.isEmpty()) {
                        Picasso.get().load(imageurls).into(mystatusimage);
                        mycaption.setText(captions);
                        time.setVisibility(View.VISIBLE);
                        time.setText(dateString + " at " + timeString);
                        linear01.setVisibility(View.VISIBLE);

                        // Assuming linear01 is a LinearLayout
                        linear01.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Bundle bundle = new Bundle();
                                bundle.putString("imageurl", imageurls);
                                bundle.putString("caption", captions);
                                bundle.putString("userid", usersid);
                                bundle.putString("dateString", dateString);
                                bundle.putString("timeString", timeString);
                                bundle.putString("statusname", statusname);
                                bundle.putString("imageurls", imageurl);

                                // Pass the Bundle to the next activity
                                Intent intent = new Intent(getContext(), StatusViewActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    } else {
                        linear01.setVisibility(View.GONE);
                    }
                }
                removeStatus(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve status", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference otherstatusref = FirebaseDatabase.getInstance().getReference("status");

        statusAdapter = new StatusAdapter(statusModelList, getContext());

        otherstatusref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    urlofimage = ds.child("imageurl").getValue(String.class);
                    String captionss = ds.child("text").getValue(String.class);
                    String usersids = ds.child("userid").getValue(String.class);
                    String timestamp = ds.child("timestamp").getValue(String.class);
                    String firstname = ds.child("firstname").getValue(String.class);
                    String lastname = ds.child("lastname").getValue(String.class);
                    String userimageurl = ds.child("userimageurl").getValue(String.class);

                    if (!usersids.equals(userId)) {
                        StatusModel statusModel = new StatusModel(usersids, urlofimage, captionss, timestamp, firstname, lastname, userimageurl);
                        statusModelList.add(statusModel);

                        // Notify the adapter that the data has changed
                        statusAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(statusAdapter);
                    }
                }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to retrieve status", Toast.LENGTH_SHORT).show();
                }
            });
        return view;

    }

    private static Date parseTimestamp(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return sdf.parse(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getDateString(Date date, Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);
        String currentStr = sdf.format(currentDate);

        if (dateStr.equals(currentStr)) {
            return "Today";
        } else {
            // Check if the date is yesterday
            long millisecondsInDay = 24 * 60 * 60 * 1000;
            if (currentDate.getTime() - date.getTime() < millisecondsInDay) {
                return "Yesterday";
            } else {
                return dateStr; // Return the date in yyyy-MM-dd format for other days
            }
        }
    }

    private static String getTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    private void removeStatus(DataSnapshot dataSnapshot) {
        // Handle the removal of a status
        // Remove the corresponding item from your statusModelList
        // and notify the adapter
        String deletedUserId = dataSnapshot.child("userid").getValue(String.class);

        for (int i = 0; i < statusModelList.size(); i++) {
            StatusModel statusModel = statusModelList.get(i);
            if (statusModel.getUserid().equals(deletedUserId)) {
                statusModelList.remove(i);
                statusAdapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}
