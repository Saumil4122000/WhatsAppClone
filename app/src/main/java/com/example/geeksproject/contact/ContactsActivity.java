package com.example.geeksproject.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geeksproject.MainActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.adapters.ContactsAdapter;
import com.example.geeksproject.model.Users;
import com.example.geeksproject.view.display.ViewImageActivity;
import com.example.geeksproject.view.profile.PofileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView courseRV;
    private ArrayList<Users> coursesArrayList;
    private ContactsAdapter courseRVAdapter;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ImageButton b=findViewById(R.id.btn_back);
        b.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        courseRV = findViewById(R.id.idRVCourses);
       loadingPB = findViewById(R.id.idProgressBar);
        db = FirebaseFirestore.getInstance();
        coursesArrayList = new ArrayList<>();
        courseRV.setHasFixedSize(true);

        courseRV.setLayoutManager(new LinearLayoutManager(this));
        courseRVAdapter = new ContactsAdapter(coursesArrayList, this);
        courseRV.setAdapter(courseRVAdapter);
        String cid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        loadingPB.setVisibility(View.GONE);
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                           if(d.getId().equals(cid)){
                               continue;
                           }
                           Users c = d.toObject(Users.class);
                            coursesArrayList.add(c);
                        }
                        courseRVAdapter.notifyDataSetChanged();
                    } else {

                        Toast.makeText(ContactsActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(ContactsActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show());



    }
}