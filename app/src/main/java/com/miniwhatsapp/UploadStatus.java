package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miniwhatsapp.Models.StatusModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadStatus extends AppCompatActivity {

    ImageView statusimage, back;
    TextView statustext, upload;
    EditText editText;
    String firstnames, lastnames, imageurls;
    String text,uploadedImageUrl, userId;
    FirebaseDatabase database;
    DatabaseReference reference;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private int selectedOrientation = ExifInterface.ORIENTATION_UNDEFINED;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadstatus_activity);

        statusimage = findViewById(R.id.statusimage);
        statustext = findViewById(R.id.statustext);
        upload = findViewById(R.id.upload);
        back = findViewById(R.id.back);

        showImagePickerDialog();

        statustext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog();
            }
        });

        SharedPreferences sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        firstnames = ds.child("firstname").getValue(String.class);
                        lastnames = ds.child("lastname").getValue(String.class);
                        String phones = ds.child("phoneNumber").getValue(String.class);
                        String emails = ds.child("email").getValue(String.class);
                        imageurls = ds.child("imageurl").getValue(String.class);
                        String idofuser = ds.child("userid").getValue(String.class);

                        Log.d("users ka data", firstnames + lastnames + phones + emails + imageurls + idofuser);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadStatus.this, "Failed to retrieve profile details", Toast.LENGTH_SHORT).show();
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("status");

                SharedPreferences sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);
                userId = sharedPreferencesss.getString("userid", "");

                // Get timestamp
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Generate a unique ID using push()
                DatabaseReference statusRef = reference.push();
                String statusid = statusRef.getKey();

                StatusModel statusModel = new StatusModel(userId, uploadedImageUrl, text, statusid, timestamp, firstnames, lastnames, imageurls);
                reference.child(userId).setValue(statusModel);

                Toast.makeText(UploadStatus.this, "Status updated successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UploadStatus.this, MainActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showImagePickerDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadStatus.this);
        builder.setTitle("Choose an option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    pickImageFromGallery();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                    finish();
                }
            }
        });

        builder.show();
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    private void showEditTextDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);

        editText = dialogView.findViewById(R.id.editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadStatus.this);
        builder.setView(dialogView)
                .setTitle("Add Text")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        text = editText.getText().toString();
                        statustext.setVisibility(View.VISIBLE);
                        statustext.setText(text);
                        upload.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        statustext.setText("");
                        upload.setVisibility(View.VISIBLE);
                    }
                });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                statusimage.setImageBitmap(imageBitmap);
                uploadImageToStorage(imageBitmap);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    statusimage.setImageBitmap(bitmap);
                    uploadImageToStorage(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImageToStorage(Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(UploadStatus.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();

        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Create a unique filename for the image
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";

        // Get a reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(fileName);

        // Upload the image
        UploadTask uploadTask = storageRef.putBytes(data);

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully, get the download URL
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadedImageUrl = uri.toString();
                        progressDialog.dismiss();
                        // Show the edit text dialog after obtaining the URL
                        showEditTextDialog();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failed image upload
                progressDialog.dismiss();
                Toast.makeText(UploadStatus.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        });
    }
}
