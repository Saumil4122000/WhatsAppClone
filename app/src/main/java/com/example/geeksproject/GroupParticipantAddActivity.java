package com.example.geeksproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.geeksproject.adapters.AdapterParticipantAdd;
import com.example.geeksproject.model.user.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupParticipantAddActivity extends AppCompatActivity {
    private RecyclerView userRv;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private String groupId,mygroupRole;
    ArrayList<Users> userList;
    private FirebaseFirestore db;
    private AdapterParticipantAdd adapterParticipantAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant_add);
        actionBar=getSupportActionBar();
        db = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        userList=new ArrayList<>();
//        actionBar.setTitle("Add Participants");
        groupId=getIntent().getStringExtra("groupId");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        userRv =findViewById(R.id.usersRv);
        loadGroupInfo();

    }

    private void getAllUsers() {

        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query reference1=firebaseFirestore.collection("Users");
        reference1.addSnapshotListener((value, error) -> {
            if (value==null){

            }
            else {
                userList.clear();
                for (DocumentSnapshot ds : value.getDocuments()) {
                    Users modelUser = ds.toObject(Users.class);
                    String uid = modelUser.getUserID().toString();
                    if (!firebaseAuth.getUid().equals(modelUser.getUserID())) {
                        userList.add(modelUser);
                      //  Log.d("TAGTAGAT",modelUser.getUserPhone()+"");
                    }
                }

                adapterParticipantAdd = new AdapterParticipantAdd(GroupParticipantAddActivity.this, userList, "" + groupId, "" + mygroupRole);
                userRv.setAdapter(adapterParticipantAdd);
            }
        });

    }
    private void loadGroupInfo() {
       final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String groupId1=""+ds.child("groupId").getValue();
                    String groupTitle=""+ds.child("groupTitle").getValue();

                   // Log.d("TATATATATA",groupId1+"JK");
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();
                    String timeStamp=""+ds.child("timestamp").getValue();
//                    actionBar.setTitle("Add Participants");

                    ref1.child(groupId1).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()){

                                    }
                                    else{
                                       // Log.d("NEW ROLE",snapshot.child("role").getValue().toString()+"Hooo");
                                        mygroupRole=""+snapshot.child("role").getValue();
//                                        actionBar.setTitle(groupTitle+"("+mygroupRole+")");

                                        getAllUsers();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}