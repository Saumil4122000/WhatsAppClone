package com.example.geeksproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class DarkModeActivity extends AppCompatActivity {
    private Button btnToggleDark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dark_mode);
       btnToggleDark=findViewById(R.id.switch_btn);
       SharedPreferences sharedPreferences=getSharedPreferences("sharedPrefs",MODE_PRIVATE);
       final SharedPreferences.Editor editor=sharedPreferences.edit();
       final boolean isDarkModeon =sharedPreferences.getBoolean("isDarkModeOn",false);
       if (isDarkModeon){
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
           btnToggleDark.setText("Disable Dark Mode");
       }
       else {
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
           btnToggleDark.setText("Enable Dark Mode");
       }
        btnToggleDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isDarkModeon){
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                   editor.putBoolean("isDarkModeOn",false);
                   editor.apply();
                   btnToggleDark.setText("Enable Dark Mode");
               }
               else{
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                   editor.putBoolean("isDarkModeOn",true);
                   editor.apply();
                   btnToggleDark.setText("Disable Dark Mode");
               }
            }
        });

    }
}