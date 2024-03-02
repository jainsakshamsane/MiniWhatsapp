package com.miniwhatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miniwhatsapp.Models.UserModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class UserDetailsActivity extends AppCompatActivity {

    TextView firstname, lastname, email, savechanges;
    ImageView uploadimage, imgview;
    Button btnupload;
    String mobile, otp, userid,imageurl;
    private Uri filePath;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetails_activity);

        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        savechanges = findViewById(R.id.savedetails);
        uploadimage = findViewById(R.id.uploadimage);
        imgview = findViewById(R.id.imgView);
        btnupload = findViewById(R.id.btnUpload);

        SharedPreferences sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE); // use intent
        mobile = sharedPreferencesss.getString("mobile", "");
        otp = sharedPreferencesss.getString("otp", "");
        userid = sharedPreferencesss.getString("userid", "");

        Log.e("idofusers", userid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();


        btnupload.setVisibility(View.GONE);
        imgview.setVisibility(View.GONE);

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });



        savechanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String namefirst = firstname.getText().toString();
                String namelast = lastname.getText().toString();
                String emails = email.getText().toString();

                // Check if any field is left blank
                if (namefirst.isEmpty() || emails.isEmpty() || namelast.isEmpty()) {
                    Toast.makeText(UserDetailsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Email validation
                if (!isValidEmail(emails)) {
                    Toast.makeText(UserDetailsActivity.this, "Invalid email address. Use @gmail.com", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing
                }

                // Check if imageurl is null or empty
                if (imageurl == null || imageurl.isEmpty()) {
                    // If the profile picture is not uploaded, show a message and return
                    Toast.makeText(UserDetailsActivity.this, "Please upload a profile picture", Toast.LENGTH_SHORT).show();
                    return;
                }


                //UserModel helperClass = new UserModel(userid, timestamp, namefirst, namelast, emails, mobile, imageurl, otp);
                reference.child(userid).child("firstname").setValue(namefirst);
                reference.child(userid).child("lastname").setValue(namelast);
                reference.child(userid).child("email").setValue(emails);
                reference.child(userid).child("imageurl").setValue(imageurl);

                Toast.makeText(UserDetailsActivity.this, "saved details successfully!", Toast.LENGTH_SHORT).show();

                // Clear input fields after successful signup
                firstname.setText("");
                lastname.setText("");
                email.setText("");

                Intent intent = new Intent(UserDetailsActivity.this, MainActivity.class);
                startActivity(intent);

                // Set the login status in shared preferences
                SharedPreferences sharedPreferencessss = getSharedPreferences("my_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferencessss.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
            }
        });
    }

    // Helper method to check if the email is in a valid format
    private boolean isValidEmail(String email) {
        return email.endsWith("@gmail.com");
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadimage.setImageBitmap(bitmap);
                imgview.setVisibility(View.GONE);
                btnupload.setVisibility(View.GONE);


                uploadImage(userid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String userid) {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    progressDialog.dismiss();
                                     imageurl = downloadUri.toString();

//                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedata", MODE_PRIVATE);
//                                    SharedPreferences.Editor editors = sharedPreferences.edit();
//                                    editors.putString("image", imageurl);
//                                    editors.apply();

                                    // Update the imageUrl field in SigninModel
                                   // updateImageUrlInModel(imageurl);

                                   // Log.e("idofusers", userid);

                                    // Save the imageUrl to the database directly
                                    reference = database.getReference("users");
                                    reference.child(userid).child("imageurl").setValue(imageurl);

                                    Toast.makeText(UserDetailsActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserDetailsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

//    private void updateImageUrlInModel(String imageurl) {
//        DatabaseReference userRef = reference.child(userid);
//        userRef.child("imageurl").setValue(imageurl);
//    }
}
