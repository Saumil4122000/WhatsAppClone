package com.example.geeksproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.geeksproject.adapters.AdapterParticipantAdd;
import com.example.geeksproject.menu.GroupChatsFragment;
import com.example.geeksproject.model.user.Users;
import com.example.geeksproject.view.GroupChat.GroupChatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.Locale;

public class GroupInfoActivity extends AppCompatActivity {
private String groupId;
private ActionBar actionBar;
ImageView groupIconTv;
RecyclerView participantRv;
private String myGroupRole="";
private FirebaseAuth firebaseAuth;
ArrayList<Users> userList;
AdapterParticipantAdd adapterParticipantAdd;

TextView descriptionTv,createdByTv,editGroupTv,PaticipantTv,addParticipantTv,leaveGroupTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        groupId=getIntent().getStringExtra("groupId");
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        groupId=getIntent().getStringExtra("groupId");
        groupIconTv=findViewById(R.id.groupIconTv);
        descriptionTv=findViewById(R.id.descriptionTv);
        createdByTv=findViewById(R.id.createdByTv);
        editGroupTv=findViewById(R.id.editGroupTv);
        addParticipantTv=findViewById(R.id.addParticipantTv);
        leaveGroupTv=findViewById(R.id.leaveGroupTv);
        PaticipantTv=findViewById(R.id.PaticipantTv);
        participantRv=findViewById(R.id.participantRv);

        userList=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupInfo();
        loadMyGroupRole();


        addParticipantTv.setOnClickListener(v -> {
            Intent intent=new Intent(GroupInfoActivity.this,GroupParticipantAddActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        });
        editGroupTv.setOnClickListener(v -> {
            Intent intent=new Intent(GroupInfoActivity.this,GroupEditActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
        });
        leaveGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogTitle="";
                String dialogueDescription="";
                String positiveButtonTitle="";
                if (myGroupRole.equals("creator")){
                    dialogTitle="Delete Group";
                    dialogueDescription="Are you sure you want to delete group permanently?";
                    positiveButtonTitle="DELETE";

                }
                else {
                    dialogTitle="Leave Group";
                    dialogueDescription="Are you sure you want to leave group ?";
                    positiveButtonTitle="Leave";

                }
                AlertDialog.Builder builder=new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dialogTitle);
                builder.setMessage(dialogueDescription).setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      if (myGroupRole.equals("creator")){
                          deleteGroup();
                      }
                      else {
                          leaveGroup();
                      }
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void leaveGroup() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(firebaseAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Deleted Successfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), GroupChatsFragment.class));
                   finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteGroup() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               Toast.makeText(getApplicationContext(),"Group Deleted Successfully",Toast.LENGTH_LONG).show();
               startActivity(new Intent(getApplicationContext(),GroupChatsFragment.class));
               finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        }
        );
    }

    private void loadMyGroupRole() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds :dataSnapshot.getChildren()){
                            myGroupRole=""+ds.child("role").getValue();
                          actionBar.setTitle(firebaseAuth.getCurrentUser().getPhoneNumber()+"("+myGroupRole+")");


                          if (myGroupRole.equals("Participants")){
                              editGroupTv.setVisibility(View.GONE);
                              addParticipantTv.setVisibility(View.GONE);
                              leaveGroupTv.setText("Leave Group");
                          }
                          else if (myGroupRole.equals("admin")){
                              editGroupTv.setVisibility(View.GONE);
                              addParticipantTv.setVisibility(View.VISIBLE);
                              leaveGroupTv.setText("Leave Group");
                          }
                          else{
                              editGroupTv.setVisibility(View.VISIBLE);
                              addParticipantTv.setVisibility(View.VISIBLE);
                              leaveGroupTv.setText("Delete Group");
                            }
                        }
                        loadParticipants();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadParticipants() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Query reference1=firebaseFirestore.collection("Users");
        reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value==null){

                }
                else {
                    userList.clear();
                    for (DocumentSnapshot ds : value.getDocuments()) {
                        Users modelUser = ds.toObject(Users.class);
                        String uid = modelUser.getUserID().toString();
                            userList.add(modelUser);
                          //  Log.d("TAGTAGAT",modelUser.getUserPhone()+"");
                    }
                    adapterParticipantAdd = new AdapterParticipantAdd(GroupInfoActivity.this, userList, "" + groupId, "" + myGroupRole);
                    participantRv.setAdapter(adapterParticipantAdd);

                }
            }
        });
//        participantRv.setAdapter(adapterParticipantAdd);
    }

    private void loadGroupInfo() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    String groupId = "" + ds.child("groupId").getValue();
                    String groupTitle = "" + ds.child("groupTitle").getValue();
                    String groupDescription = "" + ds.child("groupDescription").getValue();
                    String groupIcon = "" + ds.child("groupIcon").getValue();
                    String createdBy = "" + ds.child("createdBy").getValue();
                    String timeStamp = "" + ds.child("timeStamp").getValue();
                    actionBar.setTitle(groupTitle);
                    descriptionTv.setText(groupDescription);

                    Glide.with(getApplicationContext()).load(groupIcon).into(groupIconTv);
                    Calendar cal= Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(timeStamp));
                    String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();


                    loadCreatorInfo(dateTime,createdBy);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCreatorInfo(String dateTime, String createdBy) {
        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        assert firebaseUser != null;

        Query reference1=firebaseFirestore.collection("Users").orderBy("userID").whereEqualTo("userID",createdBy);
        reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value==null){

                }
                else {
                    for (DocumentChange dc : value.getDocumentChanges()) {

                        QueryDocumentSnapshot documentChange = dc.getDocument();
                        String userName = documentChange.getString("userName");
                         createdByTv.setText("Created By "+userName+" on "+dateTime);
                    }
                }    }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();

    }
}