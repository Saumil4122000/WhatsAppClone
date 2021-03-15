package com.example.geeksproject.menu;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.geeksproject.R;
import com.example.geeksproject.databinding.FragmentStatusBinding;
import com.example.geeksproject.view.profile.PofileActivity;
import com.example.geeksproject.view.status.DisplayStatusActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatusFragment extends Fragment {
private FragmentStatusBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatusFragment() {
        // Required empty public constructor
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_status, container, false);
        getProfile();
        binding.lnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), DisplayStatusActivity.class));
            }
        });
        return binding.getRoot();
    }

    private void getProfile()
    {

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseUser.getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name=documentSnapshot.getString("userName");

                String imageProfile = documentSnapshot.getString("imageProfile");
                String about=documentSnapshot.getString("bio");

                binding.tvName.setText(name);
                if(imageProfile.length()==0 || imageProfile==null){
                    Glide.with(getContext()).load(R.drawable.icon_male_ph).into(binding.imageProfile);
                }
                else if(imageProfile.length()!=0 || imageProfile!=null){
                    Glide.with(getContext()).load(imageProfile).into(binding.imageProfile);
                }
                if (about.length()==0 || about==null){
                    binding.tvDesc.setText("Busy");
                }
                binding.tvName.setText(name);

            }


        });
    }
}
