package com.example.geeksproject.view.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.example.geeksproject.DarkModeActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.databinding.ActivitySettingsBinding;
import com.example.geeksproject.view.profile.PofileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class SettingsActivity extends AppCompatActivity {
    private  FirebaseFirestore firebaseFirestore;
    private String cid;
    public TextView uname;
    private ActivitySettingsBinding binding;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_settings);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setTheme(android.R.style.Theme);
        if(firebaseAuth.getCurrentUser()!=null) {
            fetch();
        }
        initClick();
    }


    private void initClick() {
        binding.inProfile.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, PofileActivity.class)));
        binding.ChatsAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DarkModeActivity.class));
            }
        });
    }


    public void fetch(){


            cid=firebaseAuth.getCurrentUser().getUid();
            uname=findViewById(R.id.tv_username);
            DocumentReference documentReference=firebaseFirestore.collection("Users").document(cid);
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name=documentSnapshot.getString("userName");
                    String imageProfile = documentSnapshot.getString("imageProfile");
                    String about=documentSnapshot.getString("bio");
                    if(imageProfile.equals(" ")){
                        Glide.with(SettingsActivity.this).load(R.drawable.persin).into(binding.imageSettings);

                    }
                    if(about.equals(" ")){
                       // Glide.with(SettingsActivity.this).load(R.drawable.persin).into(binding.imageSettings);
                        binding.tvBio.setText("Busy");
                    }


                    Glide.with(SettingsActivity.this).load(imageProfile).into(binding.imageSettings);
                    uname.setText(name);
                    binding.tvBio.setText(about);
                }

                else{
                    Toast.makeText(getApplicationContext(),cid,Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),"Failed "+e.getMessage(),Toast.LENGTH_LONG).show());


    }
}