package com.example.geeksproject.view.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.geeksproject.AboutActivity;
import com.example.geeksproject.BuildConfig;
import com.example.geeksproject.R;
import com.example.geeksproject.common.Common;
import com.example.geeksproject.databinding.ActivityPofileBinding;
import com.example.geeksproject.view.display.ViewImageActivity;
import com.example.geeksproject.view.startup.SplashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class PofileActivity extends AppCompatActivity{
    public ActivityPofileBinding binding;
    private FirebaseAuth firebaseAuth;
    private  FirebaseFirestore firebaseFirestore;
    private BottomSheetDialog bottomSheetDialog,bsDialogEditName;
    private final int IMAGGE_GALLERY_REQUEST=111;
    private Uri imageUri;


    private ProgressDialog progressDialog;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pofile);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        bottomSheetDialog = new BottomSheetDialog(this);
        progressDialog = new ProgressDialog(this);

        if (firebaseAuth.getCurrentUser() != null) {
            fetch();
        }

        initActionClick();
    }


    private void showBottomSheetEditName(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener((View.OnClickListener) view1 -> bsDialogEditName.dismiss());

        final EditText edUserName = view.findViewById(R.id.ed_username);

        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edUserName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Name can'e be empty",Toast.LENGTH_SHORT).show();
                } else {
                    updateName(edUserName.getText().toString());
                    bsDialogEditName.dismiss();
                }
            }
        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bsDialogEditName.setOnDismissListener(dialog -> bsDialogEditName = null);

        bsDialogEditName.show();
    }



    private void updateName(String toString) {
        FirebaseUser cid=FirebaseAuth.getInstance().getCurrentUser();
            firebaseFirestore.collection("Users").document(cid.getUid()).update("userName",toString).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_LONG).show();
                    fetch();
                }
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show());

        }


    private void initActionClick() {
        binding.fabCamera.setOnClickListener(v -> showBottomSheetPickPhoto());
        binding.nameUpdate.setOnClickListener(v -> showBottomSheetEditName());
        binding.imageProfile.setOnClickListener(v -> {
            binding.imageProfile.invalidate();
            Drawable dr=binding.imageProfile.getDrawable();
            Common.IMAGE_BITMAP = ((BitmapDrawable)dr.getCurrent()).getBitmap();
            ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(PofileActivity.this, binding.imageProfile, "image");
            Intent intent = new Intent(PofileActivity.this, ViewImageActivity.class);
            startActivity(intent, activityOptionsCompat.toBundle());
        });
        binding.signoutBtn.setOnClickListener(v -> {
            showDialogSignOut();
        });
        binding.aboutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PofileActivity.this, AboutActivity.class));
            }
        });
    }

    private void showDialogSignOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PofileActivity.this);
        builder.setMessage("Do you want to sign out?");
        builder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PofileActivity.this, SplashScreen.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);
        view.findViewById(R.id.gallery).setOnClickListener(v -> {
            openGallery();
            bottomSheetDialog.dismiss();
        });
        view.findViewById(R.id.camera).setOnClickListener(v -> {
           chechCameraPermissio();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(dialog -> bottomSheetDialog=null);

        bottomSheetDialog.show();
    }

    private void chechCameraPermissio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                221);
        }
        else if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    222);
        }
        else
        {
            openCamera();
        }

    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            File file = File.createTempFile("IMG_" + timeStamp, ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  imageUri);
            intent.putExtra("listPhotoName", imageFileName);
            startActivityForResult(intent, 440);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void openGallery() {
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"select image"),IMAGGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGGE_GALLERY_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            uploadImagetoFirebase();
        }
        if(requestCode==440 && resultCode==RESULT_OK ){
           // imageUri=data.getData();
            uploadImagetoFirebase();
        }

    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImagetoFirebase() {
        if (imageUri!=null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

          final   StorageReference riversRef = FirebaseStorage.getInstance().getReference("ImagesProfile/" + System.currentTimeMillis()+"."+getFileExtension(imageUri));
//
            riversRef.putFile(imageUri).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url=uri.toString();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", url);

                    FirebaseUser cid=FirebaseAuth.getInstance().getCurrentUser();
                    firebaseFirestore.collection("Users").document(cid.getUid()).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            Toast.makeText(getApplicationContext(),"SUccess",Toast.LENGTH_LONG).show();
                            fetch();
                        }
                    });

                });
            });
        }
    }




    public void fetch(){


        String cid=firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(cid);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name=documentSnapshot.getString("userName");
                String phone=documentSnapshot.getString("userPhone");
                String imageProfile = documentSnapshot.getString("imageProfile");
                String about=documentSnapshot.getString("bio");
                binding.tvAbout.setText(about);
                binding.tvPhone.setText(phone);
                if(imageProfile.length()==0 || imageProfile==null){
                    Glide.with(PofileActivity.this).load(R.drawable.shraddhaimg).into(binding.imageProfile);
                }
                else if(imageProfile.length()!=0 || imageProfile!=null){
                    Glide.with(PofileActivity.this).load(imageProfile).into(binding.imageProfile);
                }
                if (about.length()==0 || about==null){
                    binding.tvAbout.setText("Busy");
                }
                binding.tvName.setText(name);

            }

            else{
                Toast.makeText(getApplicationContext(),cid,Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),"Failed "+e.getMessage(),Toast.LENGTH_LONG).show());



    }



}