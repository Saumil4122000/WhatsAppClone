package com.example.geeksproject.view.display;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.geeksproject.R;
import com.example.geeksproject.common.Common;
import com.example.geeksproject.databinding.ActivityViewImageBinding;
import com.example.geeksproject.view.profile.PofileActivity;

public class ViewImageActivity extends AppCompatActivity {
    private ActivityViewImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_image);
        binding.imageview.setImageBitmap(Common.IMAGE_BITMAP);
//        binding.btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ViewImageActivity.this, PofileActivity.class));
//            }
//        });
//    }
}}