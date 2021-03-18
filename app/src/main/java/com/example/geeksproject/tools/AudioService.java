package com.example.geeksproject.tools;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    private MediaPlayer tmpMediaPlater;
    MediaPlayer mediaPlayer=new MediaPlayer();
    private OnplayCallBack onplayCallBack;
    public AudioService(Context context) {
        this.context = context;
    }

    public void playAudioFromURL(String url, OnplayCallBack onPlayCallBack){
        this.onplayCallBack=onPlayCallBack;
        if (tmpMediaPlater!=null){
            tmpMediaPlater.stop();
        }

        try{

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            tmpMediaPlater=mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        tmpMediaPlater.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                onPlayCallBack.onFinished();
            }
        });
    }
   public interface OnplayCallBack{
        void onFinished();
   }
}
