package com.miniwhatsapp.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miniwhatsapp.ChatPage_Activity;
import com.miniwhatsapp.Models.MessageModel;
import com.miniwhatsapp.Models.UserModel;
import com.miniwhatsapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserModel> userList;
    private List<MessageModel> messagelist;
    private Context context;
    private String latestMessageTimestamp;
    private String userId;

    // Method to set the latest timestamp
    public void setLatestTimestamp(String latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp;
    }

    public UserAdapter(List<UserModel> userList, List<MessageModel> messagelist, Context context) {
        this.userList = userList;
        this.messagelist = messagelist;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wrapper_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final UserModel users = userList.get(position);
        MessageModel message = getMessageForUser(users.getUserid());



        holder.name.setText(users.getFirstname() + " " + users.getLastname());
        holder.time.setText(users.getTimestamp());
        Picasso.get().load(users.getImageurl()).into(holder.profilephoto);

        SharedPreferences sharedPreferencesss = context.getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");

        // Display the latest message text
        if (message != null) {
            String messageText = message.getMessageText();
            int maxLength = context.getResources().getInteger(R.integer.max_message_length); // Define your max length

            if (messageText.length() > maxLength) {
                messageText = messageText.substring(0, maxLength - 3) + "...";
            }

            holder.taptochat.setText(messageText);
        } else {
            holder.taptochat.setText(""); // Set to an empty string if there's no message
        }

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullImageDialog(position);
            }
        });

        // Set a click listener on the item view to navigate to the next page
        holder.linear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("firstname", users.getFirstname());
                bundle.putString("lastname", users.getLastname());
                bundle.putString("imageurl", users.getImageurl());
                bundle.putString("receiverid", users.getUserid());
                Log.e("adapter","id"+users.getUserid());
                // Start the activity and pass the Bundle
                Intent intent = new Intent(context, ChatPage_Activity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    private MessageModel getMessageForUser(String userid) {
        // Find and return the associated message for the given userId
        for (MessageModel message : messagelist) {
            if (message.getReceiverUserId().equals(userid) || message.getSenderUserId().equals(userid)) {
                return message;
            }
        }
        return null; // Return null if no message is found
    }

    private void openFullImageDialog(int position) {
        // Create the dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);

        // Find the views inside the dialog
        ImageView fullImageView = dialog.findViewById(R.id.fullImageView);

        // Get the clicked user
        final UserModel users = userList.get(position);

        // Load the actual image into the ImageView using Picasso
        Picasso.get().load(users.getImageurl())
                .placeholder(R.drawable.phone_number)
                .error(R.drawable.phone_number)
                .into(fullImageView);

        // Set a cancel listener for the dialog
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // Handle dialog cancellation if needed
            }
        });

        // Show the dialog
        dialog.show();
    }



    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Define the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, taptochat;
        ImageView profilephoto;
        LinearLayout linear3;

        CircleImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profilephoto = itemView.findViewById(R.id.profileimage);
            time = itemView.findViewById(R.id.timeofchat);
            linear3 = itemView.findViewById(R.id.linear3);
            taptochat = itemView.findViewById(R.id.taptochat);
            profileImage = itemView.findViewById(R.id.profileimage);
        }
    }
}
