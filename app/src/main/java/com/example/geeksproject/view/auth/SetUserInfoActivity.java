package com.example.geeksproject.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geeksproject.MainActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SetUserInfoActivity extends AppCompatActivity {
   // private ProgressDialog progressDialog;

private ProgressBar progressBar;
    private Button submitCourseBtn;
    private FirebaseAuth firebaseAuth;
    private String Name;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        db = FirebaseFirestore.getInstance();
        EditText userNameEdt = findViewById(R.id.ed_name);
        firebaseAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        submitCourseBtn = findViewById(R.id.btn_next);

        progressBar= new ProgressBar(this);

        //doUpdate(Name);
        submitCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // getting data from edittext fields.
                Name = userNameEdt.getText().toString();

                if (TextUtils.isEmpty(Name)) {
                    userNameEdt.setError("Please enter Course Name");
                }  else {
                    progressBar.setVisibility(View.VISIBLE);
                    // calling method to add data to Firebase Firestore.
                    addDataToFirestore(Name);
                }
            }
        });

    }

    private void addDataToFirestore(String Name) {

        String cid;
        if(firebaseAuth.getCurrentUser()!=null) {
            cid = firebaseAuth.getCurrentUser().getUid();
            CollectionReference dbCourses = db.collection("Users");
            //String uid= FirebaseAuth.getInstance().getUid();
            Users users=new Users(cid,Name,firebaseAuth.getCurrentUser().getPhoneNumber(),"","","","","","","");
            //firebaseAuth.getCurrentUser().getPhoneNumber();
            dbCourses.document(cid).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SetUserInfoActivity.this, "Your data has been added", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    Intent intent = new Intent(SetUserInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(SetUserInfoActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });


        }


    }



}











