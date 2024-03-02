package com.miniwhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.miniwhatsapp.Adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private static final String STATUS_KEY = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the "status" value to "online" in SharedPreferences
        setStatus("online");

        ViewPager viewPager = findViewById(R.id.view_pager);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        // Set titles for each tab
        tabs.getTabAt(0).setText("Chats");
        tabs.getTabAt(1).setText("Status");
        tabs.getTabAt(2).setText("Calls");
    }

    // Method to set the "status" value in SharedPreferences
    private void setStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATUS_KEY, status);
        editor.apply();
    }

    // Method to show the PopupMenu when headerImageView is clicked
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.popup_menu); // Create a menu resource file (res/menu/popup_menu.xml)

        // Set an item click listener for the PopupMenu
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item click events here
                if (item.getItemId() == R.id.menuItem1) {
                    // Handle the click on MenuItem1
                    openActivityOne();
                    return true;
                } else if (item.getItemId() == R.id.menuItem2) {
                    // Handle the click on MenuItem2
                    openActivityTwo();
                    return true;
                }
                // Add more cases as needed
                return false;
            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    // Method to open ActivityOne
    private void openActivityOne() {
        // Create an Intent to start ActivityOne
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    // Method to open ActivityTwo
    private void openActivityTwo() {
        // Create an Intent to start ActivityTwo
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
