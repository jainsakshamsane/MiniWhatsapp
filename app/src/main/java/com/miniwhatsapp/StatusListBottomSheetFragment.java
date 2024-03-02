package com.miniwhatsapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miniwhatsapp.Adapters.StatusAdapter;
import com.miniwhatsapp.Adapters.StatusViewListAdapter;
import com.miniwhatsapp.Models.StatusModel;
import com.miniwhatsapp.Models.ViewCountModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StatusListBottomSheetFragment extends BottomSheetDialogFragment {

    private OnDismissListener onDismissListener;

    RecyclerView recyclerView;
    String userId;
    List<ViewCountModel> viewCountModels = new ArrayList<>();
    StatusViewListAdapter statusViewListAdapter;

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_list_bottom_sheet, container, false);
        // Customize your bottom sheet view and handle its interactions here

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Use getContext() instead of 'this'
        recyclerView.setHasFixedSize(true);

        SharedPreferences sharedPreferencesss = getContext().getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("view");
        DatabaseReference usersDetailsRef = FirebaseDatabase.getInstance().getReference("users");

        statusViewListAdapter = new StatusViewListAdapter(viewCountModels, getContext());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String usersid = ds.child("userid").getValue(String.class);
                    String timestamp = ds.child("timestamp").getValue(String.class);

                    if (!usersid.equals(userId)){
                        // Retrieve user details from "users" node
                        usersDetailsRef.child(usersid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                // Check if the user exists in the "users" node
                                if (userSnapshot.exists()) {

                                    String userfirstName = userSnapshot.child("firstname").getValue(String.class);
                                    String userLastName = userSnapshot.child("lastname").getValue(String.class);
                                    String userimage = userSnapshot.child("imageurl").getValue(String.class);

                                    ViewCountModel viewCountModel = new ViewCountModel(usersid, userfirstName, userLastName, userimage, timestamp);
                                    viewCountModels.add(viewCountModel);

                                    // Notify the adapter that the data has changed
                                    statusViewListAdapter.notifyDataSetChanged();
                                    recyclerView.setAdapter(statusViewListAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError userError) {
                                Toast.makeText(getContext(), "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }
}
