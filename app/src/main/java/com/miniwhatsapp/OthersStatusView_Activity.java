package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miniwhatsapp.Models.StatusModel;
import com.miniwhatsapp.Models.ViewCountModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OthersStatusView_Activity extends AppCompatActivity {

    TextView time, statustext, mycaption;
    ImageView statusimage, mystatusimage, back, dotsmenu;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    RelativeLayout mainLayout;

    FirebaseDatabase database;
    DatabaseReference reference;

    String userid,activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otherstatusview_activity);

        time = findViewById(R.id.time);
        statustext = findViewById(R.id.statustext);
        statusimage = findViewById(R.id.statusimage);
        progressBar = findViewById(R.id.progressBar);
        mystatusimage = findViewById(R.id.mystatusimage);
        mycaption = findViewById(R.id.mycaption);
        back = findViewById(R.id.back);
        dotsmenu = findViewById(R.id.dotsmenu);
        mainLayout = findViewById(R.id.mainLayout);

        SharedPreferences sharedPreferencesss = getSharedPreferences("phone_details", MODE_PRIVATE);
        userid = sharedPreferencesss.getString("userid", "");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Retrieve values from the Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String imageurl = bundle.getString("imageurl", "");
            String caption = bundle.getString("caption", "");
            String timestamp = bundle.getString("timestamp", "");
            String firstname = bundle.getString("firstname", "");
            String lastname = bundle.getString("lastname", "");
            String userimageurl = bundle.getString("userimageurl", "");
            String statusof_id = bundle.getString("viewedbyid", "");
            activity = bundle.getString("count", "");

            time.setText(timestamp);
            statustext.setText(caption);
            Picasso.get().load(imageurl).into(statusimage);
            mycaption.setText(firstname + " " + lastname);
            Picasso.get().load(userimageurl).into(mystatusimage);

            database = FirebaseDatabase.getInstance();
            reference = database.getReference("view");

            // Get timestamp
            String timestamps = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            ViewCountModel viewCountModel = new ViewCountModel(statusof_id, userid, timestamps, activity);
            reference.child(userid).setValue(viewCountModel);
        }

        // Set white color for the progress
        int white = ContextCompat.getColor(this, android.R.color.white);
        ColorStateList colorStateList = ColorStateList.valueOf(white);
        progressBar.setProgressTintList(colorStateList);

        // Set up a CountDownTimer to update the progress bar every millisecond for 4 seconds
        countDownTimer = new CountDownTimer(4000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calculate progress value based on remaining time
                int progress = (int) (100 * (4000 - millisUntilFinished) / 4000);
                progressBar.setProgress(progress);
            }

            @Override
            public void onFinish() {
                finish();
            }
        }.start();

        // Add a touch listener to the main layout
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // User touched the screen, pause the countdown timer
                        countDownTimer.cancel();
                        break;
                    case MotionEvent.ACTION_UP:
                        // User released the touch, resume the countdown timer
                        countDownTimer.start();
                        break;
                }
                return true;
            }
        });
    }

    public void showDeleteMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.otherstatusmenu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuItem1) {
                    // Pause the countdown timer when MenuItem1 is clicked
                    countDownTimer.cancel();
                //    showDeleteDialog();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }
}
