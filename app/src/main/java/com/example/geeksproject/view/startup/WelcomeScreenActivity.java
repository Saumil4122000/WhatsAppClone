package com.example.geeksproject.view.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geeksproject.R;
import com.example.geeksproject.view.auth.PhoneLoginActivity;
import com.example.geeksproject.view.auth.SetUserInfoActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);


        Button button=findViewById(R.id.agree_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeScreenActivity.this,PhoneLoginActivity.class));
            }
        });
    }

}