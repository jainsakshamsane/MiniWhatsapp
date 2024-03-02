package com.miniwhatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    TextView privacypolicy, termsofservice, agreebutton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomescreen_acitivity);

        privacypolicy = findViewById(R.id.privacypolicy);
        termsofservice = findViewById(R.id.termsofservice);
        agreebutton = findViewById(R.id.agreebutton);

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        termsofservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this, TermsofServiceActivity.class);
                startActivity(intent);
            }
        });

        agreebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreenActivity.this, LoginNumberActivity.class);
                startActivity(intent);
            }
        });
    }
}
