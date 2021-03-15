package com.example.geeksproject.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.geeksproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

public class DialogReviewSendImage {
    private Context context;
    private Dialog dialog;
    //private ProgressDialog progressDialog;
    private Bitmap bitmap;
    private  ZoomageView image;
    private FloatingActionButton btnsend;
    public DialogReviewSendImage(Context context, Bitmap bitmap){
        this.context=context;
        this.bitmap=bitmap;
//        this.progressDialog=new ProgressDialog(context);
        this.dialog =new Dialog(context);
        initialization();
    }
    public void initialization(){
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_re_view_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

         image=dialog.findViewById(R.id.imageview);
         btnsend=dialog.findViewById(R.id.btn_send);
    }
    public void show(final OnCallback onCallback){
        dialog.show();
        image.setImageBitmap(bitmap);
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallback.onButtonSendClick();
                dialog.dismiss();
            }
        });
    }
    public interface OnCallback{
        void onButtonSendClick();
    }
}
