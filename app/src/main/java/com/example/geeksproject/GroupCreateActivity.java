package com.example.geeksproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.AdviceOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GroupCreateActivity extends AppCompatActivity {


    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;

    private ProgressDialog progressDialog;
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri imageUri=null;
    ImageView groupIconTv;
    EditText groupEitletext,groupdescription;
    FloatingActionButton creategrpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        groupIconTv=findViewById(R.id.groupIconTv);
        groupdescription= findViewById(R.id.groupdescription);
        creategrpButton=findViewById(R.id.creategrpButton);
       // actionBar=getSupportActionBar();
        groupEitletext=findViewById(R.id.grouptitleTv);
        //actionBar.setDisplayHomeAsUpEnabled(true);
      //  actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setTitle("Create Group");
        firebaseAuth=FirebaseAuth.getInstance();
        checkUser();

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        groupIconTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialogue();

            }
        });
        creategrpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startCreatingGroup();
            }
        });
    }

    private void startCreatingGroup() {

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Group");
        String groupTitle=groupEitletext.getText().toString().trim();
        String groupDescription=groupdescription.getText().toString().trim();
        if (groupTitle.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please add Group Namr",Toast.LENGTH_LONG).show();
            return;
        }
        String s_timestamp=""+System.currentTimeMillis();

        progressDialog.show();
       if (imageUri==null){
           createGroup(""+s_timestamp,
                   ""+groupTitle,
                   ""+groupDescription,"");
       }
       else{
           String fileNameAndPath="Group_Imgs/"+"image"+s_timestamp;
           StorageReference storageReference= FirebaseStorage.getInstance().getReference(fileNameAndPath);
           storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Task<Uri> p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                   while (!p_uriTask.isSuccessful());
                       Uri p_downloadUri=p_uriTask.getResult();
                       if (p_uriTask.isSuccessful()){
                           createGroup(""+s_timestamp,
                                   ""+groupTitle,
                                   ""+groupDescription,p_downloadUri.toString());
                       }
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_LONG).show();
               }
           });
       }
    }

    private void createGroup(String s_timeStamp,String groupTitle,String groupDescription,String groupIcon) {

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("groupId",s_timeStamp);
        hashMap.put("groupTitle",groupTitle);
        hashMap.put("groupDescription",groupDescription);
        hashMap.put("groupIcon",groupIcon);
        hashMap.put("timeStamp",s_timeStamp);
        hashMap.put("createdBy",firebaseAuth.getUid());


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(s_timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                HashMap<String,String> hashMap1=new HashMap<>();
                hashMap1.put("uid",firebaseAuth.getUid());
                hashMap1.put("role","creator");
                hashMap1.put("timestamp",s_timeStamp);
                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(s_timeStamp).child("Participants").child(firebaseAuth.getUid()).setValue(hashMap1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();

                                Toast.makeText(getApplicationContext(),"Group created..",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),"Problem occur",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showImagePickDialogue() {
        String[] options={"camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
        .setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                   if (!checkCameraPermission()){
                       requestCameraPermission();
                   }
                   else {
                       pickfromCamera();
                   }
                }
                else {
                    if (!checkStroragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickfromGallery();
                    }
                }
            }
        }).show();

    }
    private void pickfromGallery() {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,IMAGE_PICK_GALLERY_CODE);
    }


    private void pickfromCamera() {

        try {


            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
            cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityIfNeeded(intent, IMAGE_PICK_CAMERA_CODE);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        //    Log.d("cAMERA",e.getMessage());
        }
    }
private boolean checkStroragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
}
private void requestStoragePermission(){
    ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
}
private boolean checkCameraPermission(){
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);

    boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
 return result && result1;
}
private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
}
    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user!=null){
            //actionBar.setSubtitle(user.getPhoneNumber());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted && storageAccepted){
                            pickfromCamera();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Camera & storage Permission Needed",Toast.LENGTH_LONG).show();
                        }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickfromGallery();
                    }
                    else {
                        Toast.makeText(getApplicationContext()," storage Permission Needed",Toast.LENGTH_LONG).show();

                    }
                }

            }
            break;


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                    imageUri=data.getData();
                    groupIconTv.setImageURI(imageUri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                groupIconTv.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}