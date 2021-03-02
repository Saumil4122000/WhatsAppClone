package com.example.geeksproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geeksproject.view.profile.PofileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        ListView listView=findViewById(R.id.listview);
        final TextView textView=(TextView)findViewById(R.id.current_set);
        String[] about=new String[]{"Available","Busy","At school","At work","Battery about to die"
        ,"Can't talk,WhatsappOnly","In a meeting","At a gym","Sleeping","Urgent calls only","With Girlfriend"};

        List<String> about_list=new ArrayList<String>(Arrays.asList(about));
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,about_list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=(String)parent.getItemAtPosition(position);
                textView.setText(selectedItem);

                FirebaseUser cid=FirebaseAuth.getInstance().getCurrentUser();
                firebaseFirestore.collection("Users").document(cid.getUid()).update("bio",selectedItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(AboutActivity.this, PofileActivity.class));
                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show());

            }
        });
    }


}