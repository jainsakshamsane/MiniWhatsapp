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
import com.miniwhatsapp.Models.StatusModel;
import com.miniwhatsapp.Models.UserModel;
import com.miniwhatsapp.Models.ViewCountModel;
import com.miniwhatsapp.OthersStatusView_Activity;
import com.miniwhatsapp.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusViewListAdapter extends RecyclerView.Adapter<StatusViewListAdapter.ViewHolder> {

    private List<ViewCountModel> viewCountModels;
    private Context context;
    private String userId;

    public StatusViewListAdapter(List<ViewCountModel> viewCountModels, Context context) {
        this.viewCountModels = viewCountModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.wrapper_statusviewlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ViewCountModel viewCountModel = viewCountModels.get(position);
        String timestamps = viewCountModel.getTimestamp();

        // Step 1: Parse timestamp into a Date object
        Date date = parseTimestamp(timestamps);

        // Step 2: Get current date and time
        Date currentDate = new Date();

        // Step 3: Compare dates to determine if it's today, yesterday, or another day
        String dateString = getDateString(date, currentDate);

        // Step 4: Format time into 12-hour format with AM/PM
        String timeString = getTimeString(date);

        holder.name.setText(viewCountModel.getFirstname() + " " + viewCountModel.getLastname());
        holder.time.setText(dateString + " at " + timeString);
        Picasso.get().load(viewCountModel.getImageurl()).into(holder.statusimage);

        SharedPreferences sharedPreferencesss = context.getSharedPreferences("phone_details", MODE_PRIVATE);
        userId = sharedPreferencesss.getString("userid", "");
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

    @Override
    public int getItemCount() {
        return viewCountModels.size();
    }

    // Define the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, time;
        ImageView statusimage;
        LinearLayout linear3;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            statusimage = itemView.findViewById(R.id.statusimage);
            linear3 = itemView.findViewById(R.id.linear3);
        }
    }
}
