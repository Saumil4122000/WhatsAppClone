package com.example.geeksproject.view.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.example.geeksproject.InterMediateActivity;
import com.example.geeksproject.R;
import com.example.geeksproject.adapters.ChatsAdapter;
import com.example.geeksproject.databinding.ActivityChatsBinding;
import com.example.geeksproject.managers.ChatService;
import com.example.geeksproject.model.chat.Chats;
import com.example.geeksproject.service.FirebaseService;
import com.example.geeksproject.view.dialog.DialogReviewSendImage;
import com.example.geeksproject.view.profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChatsActivity extends AppCompatActivity implements ChatsAdapter.OnItemClickListner {
    private ActivityChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private  String userName;
    public static final int REQUEST_CORD_PERMISSION=332;
    private String send_img_profile;
    private ChatsAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Chats> list;
    private  String userProfile;
    private String  phone_no;
    private String bio;
    private String sTime;
    private Vibrator vibrator;
    private String audio_path;
    private MediaRecorder mediaRecorder;
    private final int IMAGGE_GALLERY_REQUEST=111;
    private boolean isActionShown=false;
    private ChatService chatService;
    private Uri imageUri;
    Intent intent;
   //ValueEventListener seenListener;
    public static final String URL="imageUri";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chats);


        initBtnClick();
        list=new ArrayList<>();
        LinearLayoutManager layoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        readChatData();
    }

    public void  sendTextMsg(String text){
        Date date= Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        String today=formatter.format(date);


        Calendar currentDateTime=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("hh:mm a");
        String currentTime=df.format(currentDateTime.getTime());
        Chats chats=new Chats(today+", "+currentTime,text,"","TEXT",firebaseUser.getUid(),receiverID);


        reference.child("Chats").push().setValue(chats).addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> Log.d("ChatServices","Error"+e.getMessage()));



//        HashMap<String,Object> hashmap=new HashMap<>();
//        hashmap.put("isseen",false);
//        reference.child("Chats").push().setValue(hashmap);
        DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);



      //  chatref1.child("chatId").push().setValue(hashmap);
        DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatref2.child("chatId").setValue(firebaseUser.getUid());


    }


    private void openGallery() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"select image"),IMAGGE_GALLERY_REQUEST);
    }
    private void initBtnClick(){

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();
        TextView tvUsername=findViewById(R.id.tv_username);
        CircularImageView imageProfile=findViewById(R.id.image_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        userName=intent.getStringExtra("userName");
        binding.btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        phone_no=intent.getStringExtra("userPhone");
        userProfile=intent.getStringExtra("userProfile");
        receiverID=intent.getStringExtra("userID");
        bio=intent.getStringExtra("bio");






        chatService=new ChatService(receiverID,this);
        if(receiverID!=null) {
            tvUsername.setText(userName);
            if (userProfile!=null) {
                if (userProfile.equals("")) {
                    binding.imageProfile.setImageResource(R.drawable.persin);
                } else {
                    Glide.with(this).load(userProfile).into(imageProfile);
                }
            }
        }
        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(binding.edMessage.getText().toString())){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.btnSend.setVisibility(View.INVISIBLE);
                        binding.recordButton.setVisibility(View.VISIBLE);
                       // binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        binding.btnSend.setVisibility(View.VISIBLE);
                        binding.recordButton.setVisibility(View.INVISIBLE);
                       // binding.btnSend.setImageDrawable(getDrawable(R.drawable.ic_baseline_send_24));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.btnSend.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                sendTextMsg(binding.edMessage.getText().toString());

                binding.edMessage.setText("");
            }
        });
        binding.btnAttachment.setOnClickListener(v -> {
            if (isActionShown){
                binding.layoutActions.setVisibility(View.GONE);
                isActionShown=false;
            }
            else {
                binding.layoutActions.setVisibility(View.VISIBLE);
                isActionShown=true;
            }
        });
        binding.toolbar.setOnClickListener(v -> startActivity(new Intent(ChatsActivity.this, UserProfileActivity.class)
                .putExtra("userID",receiverID)
                .putExtra("userProfile",userProfile)
                .putExtra("userName",userName)
                .putExtra("bio",bio)
                .putExtra("userPhone",phone_no)
        ));
        binding.recordButton.setRecordView(binding.recordView);
        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                //Start Recording..
                if (!checkPermissionFromDevice()) {
                    binding.btnEmoji.setVisibility(View.INVISIBLE);
                    binding.btnAttachment.setVisibility(View.INVISIBLE);
                    binding.btnCamera.setVisibility(View.INVISIBLE);
                    binding.edMessage.setVisibility(View.INVISIBLE);

                    startRecord();
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null) {
                        vibrator.vibrate(100);
                    }

                } else {
                    requestPermission();
                }

            }

            @Override
            public void onCancel() {
                try {
                    mediaRecorder.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);

                //Stop Recording..
                try {
                    sTime = getHumanTimeText(recordTime);
                    stopRecord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLessThanSecond() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btnEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private boolean checkPermissionFromDevice() {
        int write_external_strorage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_strorage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    private void startRecord(){
        setUpMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            //  Toast.makeText(InChatActivity.this, "Recording...", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ChatsActivity.this, "Recording Error , Please restart your app ", Toast.LENGTH_LONG).show();
        }

    }

    private void stopRecord(){
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;

                //sendVoice();
                chatService.sendVoice(audio_path);

            } else {
                Toast.makeText(getApplicationContext(), "Null", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMediaRecorder() {
        String path_save = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString() + "audio_record.m4a";
        audio_path = path_save;

        mediaRecorder = new MediaRecorder();
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(path_save);
        } catch (Exception e) {
            Log.d("setUomediaRecorder", "setUpMediaRecord: " + e.getMessage());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
        }
        //uploadImagetoFirebase();
        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            reviewImage(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void seenMessage(String userID){
//        reference=FirebaseDatabase.getInstance().getReference("Chats");
//        seenListener=reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Chats chat=snapshot.getValue(Chats.class);
//                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&  chat.getSender().equals(userID)){
//                        HashMap<String,Object> hashMap=new HashMap<>();
//                        hashMap.put("isseen",true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }


    public void readChatData(){
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats=snapshot.getValue(Chats.class);
                    try {
                        if ( chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                                || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID)
                        )
                        {
                            list.add(chats);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                }
                else{
                    DocumentReference documentReference=firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseUser).getUid());
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            send_img_profile = documentSnapshot.getString("imageProfile");
                            adapter=new ChatsAdapter(list,getApplicationContext(),send_img_profile,userProfile);
                            binding.recyclerView.setAdapter(adapter);
                            adapter.setOnItemClickListner(ChatsActivity.this);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                try {
                    mediaRecorder.reset();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void reviewImage(Bitmap bitmap){
        new DialogReviewSendImage(ChatsActivity.this,bitmap).show(new DialogReviewSendImage.OnCallback() {
            @Override
            public void onButtonSendClick() {
                if (imageUri != null) {
                     ProgressDialog progressDialog=new ProgressDialog(ChatsActivity.this);
                     progressDialog.setMessage("Sending image...");
                    new FirebaseService(ChatsActivity.this).uploadImageToFireBaseStorage(imageUri, new FirebaseService.OnCallBack() {
                        @Override
                        public void onUploadSuccess(String imageUrl) {
                            chatService.sendImage(imageUrl);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onItemClick(int position) {


        Chats  clickItem=list.get(position);
        if (clickItem.getType().equals("IMAGE")) {
            Intent i = new Intent(this, InterMediateActivity.class);
            i.putExtra(URL, clickItem.getUrl());
            startActivity(i);
        }
    }
}