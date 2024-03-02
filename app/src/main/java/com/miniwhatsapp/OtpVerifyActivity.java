package com.miniwhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OtpVerifyActivity extends AppCompatActivity {

    EditText otpEditText;
    TextView verifyTextView;
    String storedOtp,email,phoneNumber;
    String userId;
    SharedPreferences sharedPreferencesss;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verify_activity);

        otpEditText = findViewById(R.id.otp);
        verifyTextView = findViewById(R.id.confirm);

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userid");
        sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);

        Log.e("usersid", userId);

        // Retrieve the stored OTP from the "loggedin" node
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the "loggedin" node has the "otp" child
                if (dataSnapshot.child(userId).child("otp").exists()) {

                    storedOtp = dataSnapshot.child(userId).child("otp").getValue().toString();
                    email =dataSnapshot.child(userId).child("email").getValue().toString();
                    phoneNumber = dataSnapshot.child(userId).child("phoneNumber").getValue().toString();
                    //SharedPreferences sharedPreferencess = getSharedPreferences("phone_details", MODE_PRIVATE);

                    displayOtpToast(storedOtp);
                } else {
                    // Handle the case where "otp" doesn't exist
                    displayOtpToast("OTP not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
                displayOtpToast("Error retrieving OTP");
            }
        });

        // Set OnClickListener for verifyTextView
        verifyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    private void verifyOtp() {
        String enteredOtp = otpEditText.getText().toString();
        SharedPreferences.Editor editor = sharedPreferencesss.edit();
        editor.putString("mobile", phoneNumber);
        editor.putString("userid", userId);
        editor.apply();
        if (enteredOtp.equals(storedOtp)) {
            if(!email.equals("")){
                Toast.makeText(OtpVerifyActivity.this, "Phone number verified", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(OtpVerifyActivity.this, MainActivity.class);
                startActivity(intent);

                // Set the login status in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editors = sharedPreferences.edit();
                editors.putBoolean("isLoggedIn", true);
                editors.apply();
            }else{
                Toast.makeText(OtpVerifyActivity.this, "Phone number verified", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpVerifyActivity.this, UserDetailsActivity.class);
                startActivity(intent);
            }

        } else {
            Toast.makeText(OtpVerifyActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayOtpToast(String otp) {
        // Display the OTP in a Toast
        Toast.makeText(this, "Your OTP is: " + otp, Toast.LENGTH_LONG).show();
    }
}
