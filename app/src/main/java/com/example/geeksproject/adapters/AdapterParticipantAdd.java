package com.example.geeksproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.model.user.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.HolderParticipantAdd>{

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    private Context context;
    private ArrayList<Users> userlist;
    private String groupId,myGroupRole;

    public AdapterParticipantAdd(Context context, ArrayList<Users> userlist, String groupId, String myGroupRole) {
        this.context = context;
        this.userlist = userlist;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }


    @NonNull
    @Override
    public HolderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_participant_add,parent,false);
        return new HolderParticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, int position) {
        Users modelUser=userlist.get(position);
        String name=modelUser.getUserName();
        String phoneNO=modelUser.getUserPhone();
        String image=modelUser.getImageProfile();
        String uid=modelUser.getUserID();


        holder.nameTv.setText(name);
        holder.phoneTv.setText(phoneNO);
        Glide.with(context).load(image).into(holder.avtarTv);


        checkAlreadyExist(modelUser,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String hisPrevoiusRole=""+dataSnapshot.child("role").getValue();

                            String[] options;
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("Choose Option");
                            if (myGroupRole.equals("creator")){
                                if (hisPrevoiusRole.equals("admin")){
                                    options=new String[]{"Remove Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                removeAdmin(modelUser);
                                            }
                                            else{
                                                removeParticipant(modelUser);
                                            }
                                        }
                                    }).show();
                                }
                                else if (hisPrevoiusRole.equals("participant")){
                                    options=new String[]{"Make Admin","Make User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                makeAdmin(modelUser);
                                            }
                                            else{
                                                removeParticipant(modelUser);
                                            }
                                        }
                                    }).show();
                                }
                            }
                            else if (myGroupRole.equals("admin")){
                                if (hisPrevoiusRole.equals("creator")){
                                    Toast.makeText(context,"creator of Group",Toast.LENGTH_LONG).show();
                                }
                                else if (hisPrevoiusRole.equals("admin")){
                                    options=new String[]{"Remove Admin","Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                removeAdmin(modelUser);
                                            }
                                            else{
                                                removeParticipant(modelUser);
                                            }
                                        }
                                    }).show();
                                }
                                else if (myGroupRole.equals("participant")){
                                    options=new String[]{"Make Admin","Remove Participant"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){
                                                makeAdmin(modelUser);
                                            }
                                            else{
                                                removeParticipant(modelUser);
                                            }
                                        }
                                    }).show();
                                }
                            }

                        }
                        else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setTitle("Add Participant")
                                    .setMessage("Add User in this Group")
                                    .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addParticipant(modelUser);
                                        }
                                    })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert=builder.create();
                            alert.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void makeAdmin(Users modelUser) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getUserID());
        hashMap.put("role","admin");
         hashMap.put("timestamp",""+timestamp);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUserID()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"User is now Admin",Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeAdmin(Users modelUser) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getUserID());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUserID()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"User is no longer admin...",Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void removeParticipant(Users modelUser) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUserID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void addParticipant(Users modelUser) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getUserID());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUserID()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Added Successfully",Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkAlreadyExist(Users modelUser, HolderParticipantAdd holder) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String hisRole=""+dataSnapshot.child("role").getValue();
                            holder.statusTv.setText(hisRole);
                        }
                        else{
                            holder.statusTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class HolderParticipantAdd extends RecyclerView.ViewHolder{
        private ImageView avtarTv;
        private TextView nameTv,phoneTv,statusTv;
        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);
            avtarTv=itemView.findViewById(R.id.avatarTv);
            nameTv=itemView.findViewById(R.id.nameTv);
            phoneTv=itemView.findViewById(R.id.phoneTv);
            statusTv=itemView.findViewById(R.id.statusTv);
        }
    }
}
