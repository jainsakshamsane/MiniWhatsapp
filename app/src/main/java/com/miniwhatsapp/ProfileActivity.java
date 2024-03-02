package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ProfileActivity extends AppCompatActivity {

    ImageView profileimage, back, editimage, editname, editphone, editemail;
    TextView firstname, lastname, phone, email;
    String userId,uploadedImageUrl;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        profileimage = findViewById(R.id.profileimage);
        back = findViewById(R.id.back);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        editemail = findViewById(R.id.editemail);
        editimage = findViewById(R.id.editimage);
        editname = findViewById(R.id.editname);
        editphone = findViewById(R.id.editphone);

        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditFirstNameDialog();
            }
        });

        editemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditEmailDialog();
            }
        });

        editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPhoneDialog();
            }
        });

        editimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        Log.e("usersid", userId);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.orderByChild("userid").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String firstnames = ds.child("firstname").getValue(String.class);
                        String lastnames = ds.child("lastname").getValue(String.class);
                        String phones = ds.child("phoneNumber").getValue(String.class);
                        String emails = ds.child("email").getValue(String.class);
                        String imageurls = ds.child("imageurl").getValue(String.class);
                        String idofuser = ds.child("userid").getValue(String.class);

                        Log.d("users ka data", firstnames + lastnames + phones + emails + imageurls + idofuser);

                            Picasso.get().load(imageurls).into(profileimage);
                            firstname.setText(firstnames);
                            lastname.setText(lastnames);
                            phone.setText(phones);
                            email.setText(emails);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to retrieve profile details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditFirstNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit First Name");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set the default text in the input field
        input.setText(firstname.getText().toString());

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the new name
                String newName = input.getText().toString().trim();
                if(!newName.isEmpty()) {
                    updateFirstNameInDatabase(newName);
                    showEditLastNameDialog();
                } else {
                    Toast.makeText(ProfileActivity.this, "First Name cannot be blank", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditLastNameDialog();
            }
        });

        builder.show();
    }

    private void updateFirstNameInDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update the firstname in the database
        userRef.child("firstname").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI with the new name
                firstname.setText(newName);
                Toast.makeText(ProfileActivity.this, "First Name updated successfully", Toast.LENGTH_SHORT).show();
                updateFirstNameInStatusDatabase(newName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFirstNameInStatusDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("status").child(userId);

        // Update the firstname in the database
        userRef.child("firstname").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI with the new name
//                firstname.setText(newName);
//                Toast.makeText(ProfileActivity.this, "First Name updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditLastNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Last Name");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set the default text in the input field
        input.setText(lastname.getText().toString());

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the new name
                String newName = input.getText().toString().trim();
                if(!newName.isEmpty()) {
                    updateLastNameInDatabase(newName);
                } else {
                    Toast.makeText(ProfileActivity.this, "Last Name cannot be blank", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateLastNameInDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update the firstname in the database
        userRef.child("lastname").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI with the new name
                lastname.setText(newName);
                Toast.makeText(ProfileActivity.this, "Last Name updated successfully", Toast.LENGTH_SHORT).show();
                updateLastNameInStatusDatabase(newName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLastNameInStatusDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("status").child(userId);

        // Update the firstname in the database
        userRef.child("lastname").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                // Update the UI with the new name
//                lastname.setText(newName);
//                Toast.makeText(ProfileActivity.this, "Last Name updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditEmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Email");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set the default text in the input field
        input.setText(email.getText().toString());

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the new name
                String newEmail = input.getText().toString().trim();
                if (!newEmail.isEmpty()) {
                    updateEmailInDatabase(newEmail);
                } else {
                    Toast.makeText(ProfileActivity.this, "Email cannot be blank", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void updateEmailInDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update the firstname in the database
        userRef.child("email").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI with the new name
                email.setText(newName);
                Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Phone");

        // Set up the input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set the default text in the input field
        input.setText(phone.getText().toString());

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the new phone number if it's not blank
                String newPhone = input.getText().toString().trim();
                if (!newPhone.isEmpty()) {
                    updatePhoneInDatabase(newPhone);
                } else {
                    Toast.makeText(ProfileActivity.this, "Phone number cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void updatePhoneInDatabase(String newName) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update the firstname in the database
        userRef.child("phoneNumber").setValue(newName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI with the new name
                phone.setText(newName);
                Toast.makeText(ProfileActivity.this, "Phone updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickerDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                }
            }
        });

        builder.show();
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                profileimage.setImageBitmap(imageBitmap);
                uploadImageToStorage(imageBitmap);
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    profileimage.setImageBitmap(bitmap);
                    uploadImageToStorage(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadImageToStorage(Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
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

                        // Update the image URL in the database
                        updateProfileImageInDatabase(uploadedImageUrl);

                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failed image upload
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        });
    }

    private void updateProfileImageInDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Update the profile image URL in the database
        userRef.child("imageurl").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI or perform any other actions
                Toast.makeText(ProfileActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                updateProfileImageInStatusDatabase(imageUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileImageInStatusDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("status").child(userId);

        // Update the profile image URL in the database
        userRef.child("userimageurl").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update the UI or perform any other actions
//                Toast.makeText(ProfileActivity.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
