package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miniwhatsapp.Models.UserModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LoginNumberActivity extends AppCompatActivity {

    EditText phone;
    TextView sendotp;
    String userId,mobile;
    String phoneNumber, timestamp,userid ;
    DatabaseReference databaseReference;

    int i=0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginnumber_activity);

        phone = findViewById(R.id.phone);
        sendotp = findViewById(R.id.sendotp);
       databaseReference = FirebaseDatabase.getInstance().getReference("users");


    }

        public void sendOtpClick(View view) {
             phoneNumber = phone.getText().toString();

            // TODO: Validate phone number and send OTP to Firebase Realtime Database

            // Check if the phone number is empty
            if (phoneNumber.isEmpty()) {
                displayToast("Phone number can't be empty");
                return;
            }

            // Check if the phone number is less than 10 digits
            if (phoneNumber.length() < 10) {
                displayToast("Invalid phone number");
                return;
            }
            // Get timestamp
             timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            // Generate a unique ID using push()
//            DatabaseReference userRef = databaseReference.push();
//            String userId = userRef.getKey();


           // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            //cehcking all data

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {

                        mobile = ds.child("phoneNumber").getValue(String.class);


                        if(phoneNumber.equals(mobile)){
                            userid = ds.child("userid").getValue(String.class);
                           i++;
                        }
                    }
                    if(i == 1){

                        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        databaseReference.child(userid).child("otp").setValue(generateOtp());
                        Toast.makeText(LoginNumberActivity.this,"Welcome back!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginNumberActivity.this, OtpVerifyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("mobile", phoneNumber);
                        bundle.putString("userid", userid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        adddata();
                    }


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
                });



        }

    private String generateOtp() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otpNumber = 100000 + random.nextInt(900000);
        return String.valueOf(otpNumber);
    }


    private void displayToast(String message) {
        // Display the Toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void adddata(){


        DatabaseReference userRef = databaseReference.push();
        userId = userRef.getKey();


        Log.e("useridddddd", userId + phoneNumber);



        databaseReference.child(userId).child("phoneNumber").setValue(phoneNumber);
        databaseReference.child(userId).child("otp").setValue(generateOtp());
        databaseReference.child(userId).child("email").setValue("");
        databaseReference.child(userId).child("firstname").setValue("");
        databaseReference.child(userId).child("lastname").setValue("");
        databaseReference.child(userId).child("imageurl").setValue("");
        databaseReference.child(userId).child("timestamp").setValue(timestamp);
        databaseReference.child(userId).child("userid").setValue(userId);


//            UserModel helperClass = new UserModel(userId, timestamp, "", "", "", phoneNumber, "", generateOtp());
//            databaseReference.child(userId).setValue(helperClass);

        Intent intent = new Intent(LoginNumberActivity.this, OtpVerifyActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("mobile", phoneNumber);
        bundle.putString("userid", userId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
