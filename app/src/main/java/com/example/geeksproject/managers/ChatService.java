package com.example.geeksproject.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.geeksproject.interfaces.OnReadChatCallBack;
import com.example.geeksproject.model.chat.Chats;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatService {
    private Context context;
    private DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private String receiverID;

    public ChatService(String receiverID,Context context) {
        this.receiverID = receiverID;
        this.context=context;
    }
    public ChatService(Context context) {
        //this.receiverID = receiverID;
        this.context=context;
    }

    public void readChatData(OnReadChatCallBack onReadChatCallBack){
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Chats> list=new ArrayList<>();
                list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats=snapshot.getValue(Chats.class);
                    if ( chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                            || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                    )
                    {
                        list.add(chats);
                    }
                }
                onReadChatCallBack.onReadSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onReadChatCallBack.onFailure();
            }
        });
    }

//    public void  sendTextMsg(String text){
//        Date date= Calendar.getInstance().getTime();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
//        String today=formatter.format(date);
//
//
//        Calendar currentDateTime=Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("hh:mm s");
//        String currentTime=df.format(currentDateTime.getTime());
//        Chats chats=new Chats(today+", "+currentTime,text,"TEXT",firebaseUser.getUid(),receiverID);
//
//
//        reference.child("Chats").push().setValue(chats).addOnSuccessListener(aVoid -> {
//        }).addOnFailureListener(e -> Log.d("ChatServices","Error"+e.getMessage()));
//
//        DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
//        chatref1.child("chatId").setValue(receiverID);
//
//        DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
//        chatref2.child("chatId").setValue(firebaseUser.getUid());
//
//    }

    public void sendImage(String imageurl){
        Date date= Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        String today=formatter.format(date);


        Calendar currentDateTime=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("hh:mm s");
        String currentTime=df.format(currentDateTime.getTime());
        Chats chats=new Chats(getCurrentDate(),"",imageurl,"IMAGE",firebaseUser.getUid(),receiverID);


        reference.child("Chats").push().setValue(chats).addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> Log.d("ChatServices","Error"+e.getMessage()));

        DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatref1.child("chatId").setValue(receiverID);

        DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatref2.child("chatId").setValue(firebaseUser.getUid());

    }
    public String getCurrentDate(){
        Date date= Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        String today=formatter.format(date);


        Calendar currentDateTime=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("hh:mm s");
        String currentTime=df.format(currentDateTime.getTime());

        return today+","+currentTime;
    }


    public void sendVoice(String audioPath){
        final Uri uriAudio = Uri.fromFile(new File(audioPath));
        final StorageReference audioRef = FirebaseStorage.getInstance().getReference().child("Chats/Voice/" + System.currentTimeMillis());
        audioRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot audioSnapshot) {
                Task<Uri> urlTask = audioSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();
                String voiceUrl = String.valueOf(downloadUrl);

                Chats chats = new Chats(
                        getCurrentDate(),
                        "",
                        voiceUrl,
                        "VOICE",
                        firebaseUser.getUid(),
                        receiverID
                );

                reference.child("Chats").push().setValue(chats).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Send", "onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Send", "onFailure: "+e.getMessage());
                    }
                });

                //Add to ChatList
                DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
                chatRef1.child("chatid").setValue(receiverID);

                //
                DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
                chatRef2.child("chatid").setValue(firebaseUser.getUid());
            }
        });
    }
}
