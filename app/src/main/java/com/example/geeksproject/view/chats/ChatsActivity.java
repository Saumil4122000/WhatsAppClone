package com.example.geeksproject.view.chats;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.example.geeksproject.InterMediateActivity;
import com.example.geeksproject.Notification.Client;
import com.example.geeksproject.Notification.Data;
import com.example.geeksproject.Notification.Myresponse;
import com.example.geeksproject.Notification.Sender;
import com.example.geeksproject.Notification.Token;
import com.example.geeksproject.R;
import com.example.geeksproject.adapters.ChatsAdapter;
import com.example.geeksproject.databinding.ActivityChatsBinding;
import com.example.geeksproject.managers.ChatService;
import com.example.geeksproject.menu.APIService;
import com.example.geeksproject.model.chat.Chats;
import com.example.geeksproject.model.user.Users;
import com.example.geeksproject.service.FirebaseService;
import com.example.geeksproject.view.dialog.DialogReviewSendImage;
import com.example.geeksproject.view.profile.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.vanniktech.emoji.EmojiPopup;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsActivity extends AppCompatActivity implements ChatsAdapter.OnItemClickListner {
    private ActivityChatsBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String receiverID;
    private  String userName;
    public static final int REQUEST_CORD_PERMISSION=332;
    private String send_img_profile;
    private ChatsAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private List<Chats> list;
    private  String userProfile;
    private String  phone_no;
    private String bio;
    APIService apiService;
    private String sTime;
    String userid;
    boolean notify=false;
    private Vibrator vibrator;
    private String audio_path;
    private MediaRecorder mediaRecorder;
    private final int IMAGGE_GALLERY_REQUEST=111;
    private boolean isActionShown=false;
    private ChatService chatService;
    private Uri imageUri;
    public static final String URL="imageUri";
    ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_chats);
        EmojiPopup popup=EmojiPopup.Builder.fromRootView(binding.rootView).build(binding.edMessage);
        binding.btEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.toggle();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        binding.btnSend.setOnClickListener(v -> {
            notify=true;
            if (!TextUtils.isEmpty(binding.edMessage.getText().toString())){
                sendTextMsg(binding.edMessage.getText().toString());
                binding.edMessage.setText("");
            }
        });
        initBtnClick();
        list=new ArrayList<>();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);


        binding.edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    checkTypingStatus("noOne");
                }
                else{
                    checkTypingStatus(receiverID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        readChatData();
        seenMessage(receiverID);
    }

    public void  sendTextMsg(String text){
        Date date= Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter=new SimpleDateFormat("dd-MM-yyyy");
        String today=formatter.format(date);


        Calendar currentDateTime=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("hh:mm a");
        String currentTime=df.format(currentDateTime.getTime());


        Chats chats=new Chats(today+", "+currentTime,text,"","TEXT",firebaseUser.getUid(),receiverID,false);
         DatabaseReference reference=  FirebaseDatabase.getInstance().getReference();
         reference.child("Chats").push().setValue(chats).addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> Log.d("ChatServices","Error"+e.getMessage()));

        DatabaseReference chatref1=FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid()).child(receiverID);
        chatref1.child("chatId").setValue(receiverID);

        DatabaseReference chatref2=FirebaseDatabase.getInstance().getReference("ChatList").child(receiverID).child(firebaseUser.getUid());
        chatref2.child("chatId").setValue(firebaseUser.getUid());

       FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final  String msg=text;
       DocumentReference reference1=firebaseFirestore.collection("Users").document(firebaseUser.getUid());
       reference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
               if (error!=null){
                   return;
               }
               else{
                   String uname=value.getString("userName");
                   if (notify){
                       sendNotification(receiverID,uname,msg);
                   }
                   notify=false;
               }
           }
       });
    }





    private void sendNotification(String receiver, final String username,  String msg) {

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Token");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.whatsapp123, username + ": " + msg, "New Message", userId);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Myresponse>() {
                                @Override
                                public void onResponse(Call<Myresponse> call, Response<Myresponse> response) {
                                    if (response.code() == 200 ) {
                                        if (response.body().success != 1){
                                            Toast.makeText(ChatsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Myresponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        reference.removeEventListener(seenlistener);

    }
    private void updateToken(String token) {
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Token");
        Token token1 = new Token(token);
        tokenRef.child(firebaseUser.getUid()).setValue(token1);
    }
    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
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

                if (!checkPermissionFromDevice()) {
                    binding.btEmoji.setVisibility(View.INVISIBLE);
                    binding.btnAttachment.setVisibility(View.INVISIBLE);
                    //binding.btnCamera.setVisibility(View.INVISIBLE);
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
                binding.btEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
             //   binding.btnCamera.setVisibility(View.VISIBLE);
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
                binding.btEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
              //  binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });
        binding.recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                binding.btEmoji.setVisibility(View.VISIBLE);
                binding.btnAttachment.setVisibility(View.VISIBLE);
               // binding.btnCamera.setVisibility(View.VISIBLE);
                binding.edMessage.setVisibility(View.VISIBLE);
            }
        });

        }

    @Override
    protected void onStart() {
        super.onStart();
        checkTypingStatus("noOne");
        checkOnlineStatus("online");
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference ref=db.collection("Users");
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ref.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    return;
                }
                else{
                    if (value!=null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            DocumentSnapshot documentSnapshot = dc.getDocument();
                            String typingStatus = documentSnapshot.getString("typingTo");
                            String onlinestatus=documentSnapshot.getString("onlineStatus");
                            String cid = documentSnapshot.getString("userID");
                            if (cid.equals(userID)) {
                                continue;
                            } else {
                                if (cid.equals(receiverID)) {
                                    if (typingStatus.equals(userID)) {
                                        binding.tvStatus.setText("typing");
                                    } else {
                                       if (onlinestatus.equals("online")){
                                           binding.tvStatus.setText(onlinestatus);
                                       }
                                       else{
                                           //Date date= Calendar.getInstance().getTime();

                                           Calendar currentDateTime=Calendar.getInstance(Locale.ENGLISH);
                                           currentDateTime.setTimeInMillis(Long.parseLong(onlinestatus));
                                           @SuppressLint("SimpleDateFormat") SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                                           String currentTime=df.format(currentDateTime.getTime());

                                           binding.tvStatus.setText("Last Seen At "+currentTime);

                                       }
                                    }
                                }
                            }
                        }
                    }
                }
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
            //Log.d("setUomediaRecorder", "setUpMediaRecord: " + e.getMessage());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
        }

        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            reviewImage(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void seenMessage(String userID){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chat=snapshot.getValue(Chats.class);
                    assert firebaseUser != null;
                    assert userID != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&  chat.getSender().equals(userID)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void readChatData(){
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                list.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats=snapshot.getValue(Chats.class);
                    try {
                        if ( chats.getSender().equals(firebaseUser.getUid()) && chats.getReceiver().equals(receiverID)
                                || chats.getReceiver().equals(firebaseUser.getUid()) && chats.getSender().equals(receiverID))
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

    private void checkTypingStatus(String typing){
        FirebaseUser cid=FirebaseAuth.getInstance().getCurrentUser();
        assert cid != null;
        firebaseFirestore.collection("Users").document(cid.getUid()).update("typingTo",typing).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Log.d("ChatsKey","Updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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

    private void checkOnlineStatus(String status){
        FirebaseUser cid=FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore.collection("Users").document(cid.getUid()).update("onlineStatus",status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", cid.getUid());
        editor.apply();
    }
}