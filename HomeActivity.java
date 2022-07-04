package com.example.projedeneme;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ActionMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_ARRAY = "com.example.app.projedeneme.EXTRA_ARRAY";
    public static String[] commonList = new String[50];
    private InputStream inputStream;
    public ArrayList<String> sl_words = new ArrayList<>();
    public ArrayList<String> speechResult = new ArrayList<>();
    public static List<String> lemmas = new LinkedList<>();
    protected StanfordCoreNLP pipeline;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    TextView mTextArea;
    ImageButton mSpeechButton;
    private String word = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //readCSV
        inputStream = getResources().openRawResource(R.raw.sl_words);
        readCSV();

        mTextArea = findViewById(R.id.textArea);
        mSpeechButton = findViewById(R.id.speechButton);

        mSpeechButton.setOnClickListener(v -> speak());
    }

    private void speak() {

        //intents for voice recording
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Tell Me Something! :)");

        try{
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e){
            Toast.makeText(this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    //receive voice

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == RESULT_OK && null!=data){
                    //get text from intent
                    ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //view text
                    mTextArea.setText(speech.get(0));
                    getLemmasList(speech.get(0));
                    wordCheck();
                    //display video
                    Intent displayIntent = new Intent(HomeActivity.this,DisplayActivity.class);
                    displayIntent.putExtra(EXTRA_ARRAY, commonList);
                    startActivity(displayIntent);
                    finish();
                }
                break;
            }
        }
    }

    private void readCSV(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                System.out.println(csvLine);
                sl_words.add(csvLine);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }

    /*
    private void splitSpeechResult(String sentence){
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++){
            speechResult.add(words[i]);
        }
    }
    */

    private void wordCheck(){
        int counter = 0;
        word = "";
        HomeActivity activity = new HomeActivity();
        Arrays.fill(commonList, null);
        for (int i = 0; i < lemmas.size(); i++){
            word = lemmas.get(i);
            boolean contains = sl_words.contains(word);
            if(contains){
                commonList[counter] = word;
                counter++;
            }
        }
    }

    public void getLemmasList(String text) {
        lemmas.clear();

        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        // build pipeline
        pipeline = new StanfordCoreNLP(props);
        // create a document object
        CoreDocument document = pipeline.processToCoreDocument(text);
        for (CoreLabel tok : document.tokens()) {
            lemmas.add(tok.lemma());
        }
    }
}