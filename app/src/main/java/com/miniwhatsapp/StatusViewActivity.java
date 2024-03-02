package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class StatusViewActivity extends AppCompatActivity {

    TextView time, statustext, mycaption;
    ImageView statusimage, mystatusimage, back, dotsmenu;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    RelativeLayout mainLayout;

    String userid;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statusview_activity);

        time = findViewById(R.id.time);
        statustext = findViewById(R.id.statustext);
        statusimage = findViewById(R.id.statusimage);
        progressBar = findViewById(R.id.progressBar);
        mystatusimage = findViewById(R.id.mystatusimage);
        mycaption = findViewById(R.id.mycaption);
        back = findViewById(R.id.back);
        dotsmenu = findViewById(R.id.dotsmenu);
        mainLayout = findViewById(R.id.mainLayout);

        ImageView viewList = findViewById(R.id.viewlist);

        viewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusListBottomSheetFragment bottomSheetFragment = new StatusListBottomSheetFragment();

                // Set the OnDismissListener
                bottomSheetFragment.setOnDismissListener(new StatusListBottomSheetFragment.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Resume your countdown timer here
                        countDownTimer.start();
                    }
                });

                // Show the bottom sheet fragment
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

                // Pause your countdown timer here
                countDownTimer.cancel();
            }
        });

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
            userid = bundle.getString("userid", "");
            String dateString = bundle.getString("dateString", "");
            String timeString = bundle.getString("timeString", "");
            String statusname = bundle.getString("statusname", "");
            String imagekaurl = bundle.getString("imageurls", "");

            time.setText(dateString + " at " + timeString);
            statustext.setText(caption);
            Picasso.get().load(imageurl).into(statusimage);
            Picasso.get().load(imagekaurl).into(mystatusimage);
            mycaption.setText(statusname);
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
        popupMenu.inflate(R.menu.status_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menuItem1) {
                    // Pause the countdown timer when MenuItem1 is clicked
                    countDownTimer.cancel();
                    showDeleteDialog();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Status");
        builder.setMessage("Are you sure you want to delete this status?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, delete the status
                deleteStatus();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, dismiss the dialog
                dialog.dismiss();
                // Resume the countdown timer
                countDownTimer.start();
            }
        });
        builder.show();
    }

    private void deleteStatus() {
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("status").child(userid);
            statusRef.removeValue();
            // Now finish the activity
            Intent intent = new Intent(StatusViewActivity.this, MainActivity.class);
            startActivity(intent);
    }
}
