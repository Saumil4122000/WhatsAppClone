package com.example.geeksproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.geeksproject.common.Common;
import com.example.geeksproject.view.display.ViewImageActivity;
import com.example.geeksproject.view.profile.PofileActivity;

import static com.example.geeksproject.view.chats.ChatsActivity.URL;

public class InterMediateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_mediate);
        Intent i=getIntent();
        String URL1=i.getStringExtra(URL);
        ImageView imageView=findViewById(R.id.imageView2);
        Glide.with(getApplicationContext()).load(URL1).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.invalidate();
                Drawable dr=imageView.getDrawable();
                Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(InterMediateActivity.this,imageView, "image");
                Intent intent = new Intent(InterMediateActivity.this, ViewImageActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });

    }
}