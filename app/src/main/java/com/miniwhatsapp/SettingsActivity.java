package com.miniwhatsapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    TextView logout;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        logout = findViewById(R.id.logout);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(v -> logoutUser());
    }

        private void logoutUser() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");
            builder.setPositiveButton("Yes", (dialog, which) -> logout());
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all preferences, including the switch state
        editor.clear();
        editor.apply();

        // Navigate to the login page
        Intent intent = new Intent(SettingsActivity.this, LoginNumberActivity.class);
        Toast.makeText(SettingsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
