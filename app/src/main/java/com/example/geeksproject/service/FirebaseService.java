package com.example.geeksproject.service;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.geeksproject.model.StatusModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseService {
    private Context context;
    public FirebaseService(Context context){
        this.context=context;
    }
    public void uploadImageToFireBaseStorage(Uri uri, final OnCallBack onCallBack){
        StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("ImagesChats/" + System.currentTimeMillis()+"."+getFileExtension(uri));
        riversRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!urlTask.isSuccessful());
            Uri downloadUrl = urlTask.getResult();

            final String sdownload_url = String.valueOf(downloadUrl);

            onCallBack.onUploadSuccess(sdownload_url);

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCallBack.onUploadFailed(e);
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=context.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public interface OnCallBack{
        void onUploadSuccess(String imageUrl);
        void onUploadFailed(Exception e);
    }
    public void addNewStatus(StatusModel statusModel,OnAddStatusCallback onAddStatusCallback){
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Status").document(statusModel.getId()).set(statusModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onAddStatusCallback.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"something went wrong",Toast.LENGTH_LONG).show();
            }
        });

    }
    public interface OnAddStatusCallback{
        void onSuccess();
        void onFailed(Exception e);
    }
}
