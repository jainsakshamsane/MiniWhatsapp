package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.miniwhatsapp.Adapters.RecieverAdapter;
import com.miniwhatsapp.Adapters.SenderAdapter;
import com.miniwhatsapp.Models.MessageModel;
import com.squareup.picasso.Picasso;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPage_Activity extends AppCompatActivity {

    ImageView profileimage, back, callbutton, sendbutton;
    TextView name;
    EditText message;
    String userId;
    String receiverid, imageurls;

    RecyclerView recyclerView, recyclerView1;
    SenderAdapter senderAdapter;
    RecieverAdapter receiverAdapter;

    List<MessageModel> senderMessages = new ArrayList<>();
  //  List<MessageModel> receiverMessages = new ArrayList<>();

    DatabaseReference databaseReference;

    SharedPreferences sharedPreferencesss;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatpage_activity);

        profileimage = findViewById(R.id.profileimage);
        back = findViewById(R.id.back);
        message = findViewById(R.id.message);
        name = findViewById(R.id.name);
        callbutton = findViewById(R.id.callbutton);
        sendbutton = findViewById(R.id.sendbutton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatPage_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView); // Add your RecyclerView ID
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        senderAdapter = new SenderAdapter(senderMessages,userId, this); // Create your custom sender adapter
        recyclerView.setAdapter(senderAdapter); // Set sender adapter by default

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String firstnames = bundle.getString("firstname");
            String lastnames = bundle.getString("lastname");
            imageurls = bundle.getString("imageurl");
            receiverid = bundle.getString("receiverid");
            Log.e("chatpage", "id" + receiverid);
            name.setText(firstnames + " " + lastnames);
            Picasso.get().load(imageurls).into(profileimage);
        }

        // Initialize your Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(message.getText().toString());
            }
        });
     //   senderMessages.clear();
        showsendermessage();


        Log.e("oncerate","test");
        CircleImageView profileImage = findViewById(R.id.profileimage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullImageDialog();
            }
        });

    }


    private void openFullImageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);

        ImageView fullImageView = dialog.findViewById(R.id.fullImageView);
        // Load the actual image into the ImageView using Picasso
        Picasso.get().load(imageurls)
                .placeholder(R.drawable.phone_number)
                .error(R.drawable.phone_number)
                .into(fullImageView);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // Handle dialog cancellation if needed
            }
        });

        dialog.show();
    }


    private void sendMessage(String messageText) {

        Log.e("sendmessage","my test");

        String senderUserId = userId;
        String receiverUserId = receiverid;

        // Create a unique key for the message
        String messageId = databaseReference.child("message").child(senderUserId + "_" + receiverUserId).push().getKey();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Construct the message data
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("messageText", messageText);
        messageData.put("timestamp", timestamp);
        messageData.put("senderUserId", senderUserId);
        messageData.put("receiverUserId", receiverUserId);


//        MessageModel messages = new MessageModel();
//        messages.setMessageText(messageText);
//        messages.setTimestamp(timestamp);
//        messages.setSenderUserId(senderUserId);
//        messages.getReceiverUserId();
        // Push the message data to the appropriate location

        databaseReference.child("message").child(senderUserId + "_" + receiverUserId).child(messageId).setValue(messageData);
       // senderMessages.add(messages);
//        senderAdapter.notifyDataSetChanged();
        showsendermessage();
        scrollToLastItem();
        // Clear the EditText after sending the message
        message.setText("");
    }

    private void showsendermessage() {
        Log.e("showsendermessage","my test");
        userId = sharedPreferencesss.getString("userid", "");
        //senderMessages.clear();
        String path1 = userId + "_" + receiverid;
        String path2 =  receiverid + "_" + userId;

        databaseReference.child("message").child(path1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                senderMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = snapshot.getValue(MessageModel.class);

                        if (message.getSenderUserId().equals(userId)) {
                            senderMessages.add(message);
                    }
                }
                //updateMessages();
                //senderAdapter.notifyDataSetChanged();
                //senderAdapter.setMessages(senderMessages);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        databaseReference.child("message").child(path2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //senderMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = snapshot.getValue(MessageModel.class);

                    if (message.getSenderUserId().equals(receiverid)) {
                        // receiverMessages.add(message);
                        senderMessages.add(message);
                    }
                }
                //receiverAdapter.setMessages(receiverMessages);
                updateMessages();
                scrollToLastItem();
                //senderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        //updateMessages();
    }



    private void updateMessages() {
        Log.e("update","my test");
        // Sort the messages based on timestamp
        Collections.sort(senderMessages, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel message1, MessageModel message2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = format.parse(message1.getTimestamp());
                    Date date2 = format.parse(message2.getTimestamp());
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // Update the RecyclerView with the sorted list

        senderAdapter.notifyDataSetChanged();
    }

    private void scrollToLastItem() {
        // Scroll to the last item in the RecyclerView
        recyclerView.scrollToPosition(senderMessages.size() - 1);
    }
}
