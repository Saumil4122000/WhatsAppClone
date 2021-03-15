package com.example.geeksproject.view.profile;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.common.Common;
import com.example.geeksproject.databinding.ActivityUserProfileBinding;
import com.example.geeksproject.view.display.ViewImageActivity;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    private ActivityUserProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_user_profile);


        Intent i=getIntent();
        String userID =i.getStringExtra("userID");
        String userName=i.getStringExtra("userName");
        String userProfile=i.getStringExtra("userProfile");
        String  userPhone=i.getStringExtra("userPhone");
        String bio=i.getStringExtra("bio");
        if(userID!=null) {
            binding.toolbar.setTitle(userName);
            binding.tvPhone.setText(userPhone);
            binding.tvDesc.setText(bio);
            if (userProfile!=null) {
                if (userProfile.equals("")) {
                    binding.imageProfile.setImageResource(R.drawable.persin);
                } else {
                    Glide.with(this).load(userProfile).into(binding.imageProfile);
                }
            }
        }
    binding.imageProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            binding.imageProfile.invalidate();
            Drawable dr=binding.imageProfile.getDrawable();
            Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(UserProfileActivity.this, binding.imageProfile, "image");
            Intent intent = new Intent(UserProfileActivity.this, ViewImageActivity.class);
            startActivity(intent, activityOptionsCompat.toBundle());
        }
    });
        initToolbar();
    }

    private void initToolbar() {

        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}