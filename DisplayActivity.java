package com.example.projedeneme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayActivity extends AppCompatActivity {

    TextView mTextView;
    private boolean finish = false;
    private int counter = 0;
    private String word = "";
    //private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        mTextView = findViewById(R.id.textView);
        Intent intent = getIntent();
        String[] commonList = intent.getStringArrayExtra(HomeActivity.EXTRA_ARRAY);
        VideoView videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        word = commonList[counter];
        mTextView.setText(word);
        if(word.equals("I")){
            word = "iword";
        }
        else if(word.equals("break") || word.equals("case")|| word.equals("catch") || word.equals("do") || word.equals("if") || word.equals("new") || word.equals("this") || word.equals("try") || word.equals("while") || word.equals("long") || word.equals("for") || word.equals("finally") || word.equals("else") || word.equals("continue")){
            word = word.concat("word");
        }
        String uriPath = "android.resource://"+getPackageName()+"/raw/"+ word;
        Uri UrlPath= Uri.parse(uriPath);
        videoView.setVideoURI(UrlPath);
        try{
            videoView.requestFocus();
            counter++;
            videoView.start();
        }catch (Exception e){
            System.out.printf("Error playing video %s\n", e);
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if(commonList[counter] != null){
                    word = commonList[counter];
                    mTextView.setText(word);
                    if(word.equals("I")){
                        word = "iword";
                    }
                    else if(word.equals("break") || word.equals("case")|| word.equals("catch") || word.equals("do") || word.equals("if") || word.equals("new") || word.equals("this") || word.equals("try") || word.equals("while") || word.equals("long") || word.equals("for") || word.equals("finally") || word.equals("else") || word.equals("continue")){
                        word = word.concat("word");
                    }
                    String uriPath = "android.resource://"+getPackageName()+"/raw/"+ word;
                    Uri UrlPath= Uri.parse(uriPath);
                    videoView.setVideoURI(UrlPath);
                    try{
                        videoView.requestFocus();
                        counter++;
                        videoView.start();
                    }catch (Exception e){
                        System.out.printf("Error playing video %s\n", e);
                    }
                }
                else{
                    Intent homeIntent = new Intent(DisplayActivity.this,HomeActivity.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        });
    }
}