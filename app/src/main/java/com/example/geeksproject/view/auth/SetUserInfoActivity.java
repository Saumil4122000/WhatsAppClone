package com.example.geeksproject.view.auth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.geeksproject.BuildConfig;
import com.example.geeksproject.MainActivity;
import com.example.geeksproject.R;

import com.example.geeksproject.databinding.ActivitySetUserInfoBinding;
import com.example.geeksproject.model.user.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class SetUserInfoActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    ImageView image_profile;
    private ProgressDialog progressDialog;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private String[] cameraPermission;
    private String[] storagePermission;
    private FirebaseFirestore db;
    EditText userNameEdt;
    private Uri imageUri = null;
    private FirebaseAuth firebaseAuth;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        db = FirebaseFirestore.getInstance();
        userNameEdt = findViewById(R.id.ed_name);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nextBtn = findViewById(R.id.btn_next);


        image_profile = findViewById(R.id.image_profile);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(true);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        loadGroupInfo();

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialogue();

            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });
    }

    private void loadGroupInfo() {
        String cid = firebaseAuth.getCurrentUser().getUid();
        Query reference1 = db.collection("Users").orderBy("userID").whereEqualTo("userID", cid);
        reference1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value == null) {

                } else {
                    for (DocumentChange dc : value.getDocumentChanges()) {
//
                        QueryDocumentSnapshot documentChange = dc.getDocument();
                        String userName = documentChange.getString("userName");
                        String userProfile = documentChange.getString("imageProfile");
                        if (userProfile.isEmpty()) {
                            Glide.with(getApplicationContext()).load(R.drawable.maleuserprofilepicture).into(image_profile);
                        } else {
                            Glide.with(getApplicationContext()).load(userProfile).into(image_profile);
                        }
                        userNameEdt.setText(userName);
                    }
                }
            }
        });
    }

    private void startUpdatingGroup() {
        String groupTitle = userNameEdt.getText().toString().trim();


        if (TextUtils.isEmpty(groupTitle)) {
            Toast.makeText(getApplicationContext(), "UserName can not be Empty", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Updating Profile");
        progressDialog.show();
        if (imageUri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userName", groupTitle);
            FirebaseUser cid = FirebaseAuth.getInstance().getCurrentUser();
            db.collection("Users").document(cid.getUid()).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String filePathName = "ProfileImage/" + "image" + "_" + timeStamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseUser cid = FirebaseAuth.getInstance().getCurrentUser();

                    Task<Uri> p_taskUri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!p_taskUri.isSuccessful()) ;
                    Uri p_downloadUrl = p_taskUri.getResult();
                    if (p_taskUri.isSuccessful()) {


                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userName", groupTitle);
                        hashMap.put("imageProfile", "" + p_downloadUrl);
                        db.collection("Users").document(cid.getUid()).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showImagePickDialogue() {
        String[] options = {"camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (!checkCameraPermission()) {
                                requestCameraPermission();
                            } else {
                                pickfromCamera();
                            }
                        } else {
                            if (!checkStroragePermission()) {
                                requestStoragePermission();
                            } else {
                                pickfromGallery();
                            }
                        }
                    }
                }).show();

    }

    private void pickfromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStroragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
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
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            // Log.d("cAMERA",e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data.getData();
                image_profile.setImageURI(imageUri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                image_profile.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String cid = firebaseAuth.getCurrentUser().getUid();
        db.collection("Users").document(cid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        startUpdatingGroup();
                    } else {
                        Newregister();
                    }
                }
            }
        });


    }

    private void Newregister() {
        progressDialog.setMessage("Updating Profile");
        progressDialog.show();
        String cid = firebaseAuth.getCurrentUser().getUid();
        String name = userNameEdt.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Name can't be empty", Toast.LENGTH_LONG).show();
        }

        if (imageUri == null) {
            Users users = new Users(cid, name, firebaseAuth.getCurrentUser().getPhoneNumber(), "", "", "", "", "", "", "");

            db.collection("Users").document(cid).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(SetUserInfoActivity.this, "You are now Regisitered", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetUserInfoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetUserInfoActivity.this, "Fail to add you ,Please try again \n" + e, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String filePathName = "ProfileImage/" + "image" + "_" + timeStamp;
            String cid1 = firebaseAuth.getCurrentUser().getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> p_taskUri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!p_taskUri.isSuccessful()) ;
                    Uri p_downloadUrl = p_taskUri.getResult();
                    String UriImage = p_downloadUrl.toString();
                    if (p_taskUri.isSuccessful()) {
                        Users users = new Users(cid1, name, firebaseAuth.getCurrentUser().getPhoneNumber(), UriImage, "", "", "", "", "", "");

                        db.collection("Users").document(cid1).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SetUserInfoActivity.this, "You are now Regisitered", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(SetUserInfoActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SetUserInfoActivity.this, "Fail to add you ,Please try again \n" + e, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }

            });
        }
    }
}

















































































    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // private ProgressDialog progressDialog;
//
//    private ProgressBar progressBar;
//    private Button submitCourseBtn;
//    private FirebaseAuth firebaseAuth;
//    private String Name;
//
//
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_set_user_info);
//
//        db = FirebaseFirestore.getInstance();
//        EditText userNameEdt = findViewById(R.id.ed_name);
//        firebaseAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        submitCourseBtn = findViewById(R.id.btn_next);
//
//        progressBar = new ProgressBar(this);
//
//        //doUpdate(Name);
//        submitCourseBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // getting data from edittext fields.
//                Name = userNameEdt.getText().toString();
//
//                if (TextUtils.isEmpty(Name)) {
//                    userNameEdt.setError("Please enter Course Name");
//                } else {
//                    progressBar.setVisibility(View.VISIBLE);
//                    // calling method to add data to Firebase Firestore.
//                    addDataToFirestore(Name);
//                }
//            }
//        });
//
//    }
//
//    private void addDataToFirestore(String Name) {
//
//        String cid;
//        if (firebaseAuth.getCurrentUser() != null) {
//            cid = firebaseAuth.getCurrentUser().getUid();
//            CollectionReference dbCourses = db.collection("Users");
//            //String uid= FirebaseAuth.getInstance().getUid();
//            Users users = new Users(cid, Name, firebaseAuth.getCurrentUser().getPhoneNumber(), "", "", "", "", "", "", "");
//            //firebaseAuth.getCurrentUser().getPhoneNumber();
//            dbCourses.document(cid).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Toast.makeText(SetUserInfoActivity.this, "Your data has been added", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//
//                    Intent intent = new Intent(SetUserInfoActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//
//                    Toast.makeText(SetUserInfoActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }
//            });
//
//
//        }
//    }







