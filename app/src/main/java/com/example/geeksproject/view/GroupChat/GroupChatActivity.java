package com.example.geeksproject.view.GroupChat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.geeksproject.GroupInfoActivity;
import com.example.geeksproject.GroupParticipantAddActivity;
import com.example.geeksproject.Notification.Data;
import com.example.geeksproject.R;
import com.example.geeksproject.adapters.AdapterGroupChat;
import com.example.geeksproject.model.ModelGroupChat;
import com.google.android.datatransport.cct.CCTDestination;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import io.grpc.Context;

public class GroupChatActivity extends AppCompatActivity {
private String groupId,myGroupRole;
private Toolbar toolbar;
private ImageView groupIconTv;
private ImageButton attchbtn;
FloatingActionButton sendBtn;
private TextView groupTitleTv;
private EditText messageEt;
FirebaseAuth firebaseAuth;
RecyclerView chatRv;
private static final int CAMERA_REQUEST_CODE=200;
private static final int STORAGE_REQUEST_CODE=400;
    private static final int IMAGE_PICK_GALLERY_CODE=1000;
    private static final int IMAGE_PICK_CAMERA_CODE=2000;
String[] cameraPermission;
String[] storagePermission;
private Uri image_Url=null;

private ArrayList<ModelGroupChat> groupChatList;
private AdapterGroupChat adapterGroupChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
       toolbar=findViewById(R.id.toolbar);
        groupIconTv=findViewById(R.id.groupIconTv);
        attchbtn=findViewById(R.id.btn_attachment);
        groupTitleTv=findViewById(R.id.grouptitleTv);
        sendBtn=findViewById(R.id.sendBtn);
        messageEt=findViewById(R.id.messageEt);
        chatRv=findViewById(R.id.chatRv);
        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");

//         setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        cameraPermission=new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission=new  String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };




        LinearLayoutManager layoutManager=new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(false);
        chatRv.setLayoutManager(layoutManager);


        loadGroupInfo();
        loadGroupMessages();
        loadMyGroupRole();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messageEt.getText().toString().trim();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(getApplicationContext(),"Can not send Empty Message",Toast.LENGTH_LONG).show();
                }
                else{
                    sendMessage(message);
                }
            }
        });
        attchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showImageImportDialogue();
            }
        });
    }

    private void showImageImportDialogue() {
        String[] options ={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickCamera();
                    }
                }
                else{
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickGallery();
                    }
                }
            }
        }).show();
    }
private void pickGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("images/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
}
private void pickCamera(){
    ContentValues contentValues=new ContentValues();
    contentValues.put(MediaStore.Images.Media.TITLE,"GroupImageTitle");
    contentValues.put(MediaStore.Images.Media.DESCRIPTION,"GroupImageDescription");


    image_Url=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT,image_Url);
    startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
}

private void requestStoragePermission(){
    ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
}
private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
}
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return  result && result1;
}

    private void loadGroupMessages() {

        groupChatList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChatList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelGroupChat model=ds.getValue(ModelGroupChat.class);
                    groupChatList.add(model);
                }

                adapterGroupChat=new AdapterGroupChat(GroupChatActivity.this,groupChatList);
                chatRv.setAdapter(adapterGroupChat);
                chatRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
String timestamp=System.currentTimeMillis()+"";
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",""+firebaseAuth.getUid());
        hashMap.put("message",""+message);
        hashMap.put("timeStamp",""+timestamp);
        hashMap.put("type",""+"text");


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp)
                .setValue(hashMap)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                        messageEt.setText("");
                   }
               }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
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
                            invalidateOptionsMenu();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupInfo() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String timeStamp=""+ds.child("timeStamp").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();

                    groupTitleTv.setText(groupTitle);
                    Glide.with(getApplicationContext()).load(groupIcon).into(groupIconTv);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                image_Url=data.getData();
              sendImageMessage();
            }
            if (requestCode==IMAGE_PICK_CAMERA_CODE){
                sendImageMessage();
            }
        }
    }

    private void sendImageMessage() {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.setTitle("Sending image..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        String filenamePath="ChatImages/"+""+System.currentTimeMillis();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(filenamePath);
        storageReference.putFile(image_Url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTask.isSuccessful());
                Uri p_downloadUrl=p_uriTask.getResult();
                if (p_uriTask.isSuccessful()){
                    String timestamp=""+System.currentTimeMillis();

                    HashMap<String,Object> hashMap=new HashMap<>();
                    hashMap.put("sender",""+firebaseAuth.getUid());
                    hashMap.put("message",""+p_downloadUrl);
                    hashMap.put("timestamp",""+timestamp);
                    hashMap.put("type",""+"text");


                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                    ref.child(groupId).child("Messages").child(timestamp)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    messageEt.setText("");
                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.action_add_participant){
           // Toast.makeText(getApplicationContext(),"ADDED",Toast.LENGTH_LONG).show();
            Intent i=new Intent(this, GroupParticipantAddActivity.class);
            i.putExtra("groupId",groupId);
            startActivity(i);
        }
        else if(id==R.id.action_group_info){
            Intent i=new Intent(this, GroupInfoActivity.class);
            i.putExtra("groupId",groupId);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group,menu);
//      menu.findItem(R.id.menu_search).setVisible(false);
        //Log.d("TATATATA",myGroupRole+"hiiiiii");
        if (myGroupRole.equals("creator") || myGroupRole.equals("admin")){
            menu.findItem(R.id.action_add_participant).setVisible(true);

        }
        else {
            menu.findItem(R.id.action_add_participant).setVisible(false);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"STORAGE AND CAMERA PERMISSION NEEDED",Toast.LENGTH_LONG).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if ( writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"STORAGE PERMISSION NEEDED",Toast.LENGTH_LONG).show();
                    }

                }
            }
            break;
        }
    }
}