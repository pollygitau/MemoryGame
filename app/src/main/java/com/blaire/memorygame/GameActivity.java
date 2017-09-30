package com.blaire.memorygame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    Animation wobble;


    //objects to edit our file
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "MyData";
    String intName = "MyInt";
    int defaultInt = 0;
    int hiScore;
    LinearLayout layout;

    //prepare objects and sound references
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;
    //UI
    TextView textScore;
    TextView textDifficulty;
    TextView textWatchGo;
    //Buttons
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button buttonReplay;
    //variables for our thread
    int difficultyLevel = 3;
    //An array to hold the randomly generated sequence
    int[] sequenceToCopy = new int[100];
    private Handler myHandler;
    //Are we playing a sequence at the moment?
    boolean playSequence = false;
    //and which element of the sequence are we on
    int elementToPlay = 0;
    //checking the players answers
    int playerResponses;
    int playerScore;
    boolean isResponding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wobble = AnimationUtils.loadAnimation(this, R.anim.wobble);
        //initialize two SharedPreferences objects
        prefs = getSharedPreferences(dataName,MODE_PRIVATE);
        editor = prefs.edit();
        hiScore = prefs.getInt(intName, defaultInt);
        layout = (LinearLayout)findViewById(R.id.linearLayout);
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sample1 = soundPool.load(this, R.raw.sample1, 0);
        sample2 =soundPool.load(this,R.raw.sample2,0);
        sample3 = soundPool.load(this, R.raw.sample3,0);
        sample4 = soundPool.load(this, R.raw.sample4,0);

//reference all the elements of our UI
        textScore = (TextView) findViewById(R.id.textScore);
        textScore.setText("Score: " + playerScore);

        textDifficulty = (TextView) findViewById(R.id.textDifficulty);
        textDifficulty.setText("Level: " + difficultyLevel);

        textWatchGo = (TextView) findViewById(R.id.textWatchGo);

//set the buttons
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        buttonReplay = (Button) findViewById(R.id.buttonReplay);

//set all buttons to listen for clicks:
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        buttonReplay.setOnClickListener(this);

        //code to define our thread
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (playSequence) {
                    //Make sure all the buttons are visible
//                    button1.setVisibility(View.VISIBLE);
//                    button2.setVisibility(View.VISIBLE);
//                    button3.setVisibility(View.VISIBLE);
//                    button4.setVisibility(View.VISIBLE);

                    switch (sequenceToCopy[elementToPlay]) {
                        case 1:
                            //hide a button
                            button1.startAnimation(wobble);
                            //button1.setVisibility(View.INVISIBLE);
                            //play a sound
                            soundPool.play(sample1, 1, 1, 0, 0, 1);
                            break;

                        case 2:
                            //hide a button
                            button2.startAnimation(wobble);
                            //button2.setVisibility(View.INVISIBLE);
                            //play a sound
                            soundPool.play(sample2, 1, 1, 0, 0, 1);
                            break;

                        case 3:
                            //hide a button
                            button3.startAnimation(wobble);
                            //button3.setVisibility(View.INVISIBLE);
                            //play a sound
                            soundPool.play(sample3, 1, 1, 0, 0, 1);
                            break;

                        case 4:
                            //hide a button
                            button4.startAnimation(wobble);
                            //button4.setVisibility(View.INVISIBLE);
                            //play a sound
                            soundPool.play(sample4, 1, 1, 0, 0, 1);
                            break;
                    }
                    elementToPlay++;
                    if (elementToPlay == difficultyLevel) {
                        sequenceFinished();
                    }
                }
                myHandler.sendEmptyMessageDelayed(0, 900);
            }
        };//end of thread
        myHandler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View view) {
        if (!playSequence) {
            //only accept if the sequence is not playing
            switch (view.getId()) {
                case R.id.button1:
                    soundPool.play(sample1, 1, 1, 0, 0, 1);
                    checkElement(1);//checkElement() to be implemented
                    break;

                case R.id.button2:
                    soundPool.play(sample2, 1, 1, 0, 0, 1);
                    checkElement(2);
                    break;

                case R.id.button3:
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    checkElement(3);
                    break;

                case R.id.button4:
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                    checkElement(4);
                    break;

                case R.id.buttonReplay:
                    difficultyLevel = 3;
                    //initialize two shared preference objects
                    prefs = getSharedPreferences(dataName, MODE_PRIVATE);
                    //load our high score or in unavailable, our default of 0
                    hiScore = prefs.getInt(intName, defaultInt);
                    playerScore = hiScore;
                    textScore.setText("Score: " + playerScore);
                    playASequence();
                    break;
            }
        }
    }

    public void createSequence() {
        //for choosing a random button
        Random randInt = new Random();
        int ourRandom;
        for (int i = 0; i < difficultyLevel; i++) {
            //get a random number between 1 and 4
            ourRandom = randInt.nextInt(4);
            ourRandom++; //make sure its not 0
            //save the nimber to array
            sequenceToCopy[i] = ourRandom;
        }
    }

    public void playASequence() {
        createSequence();
        isResponding = false;
        elementToPlay = 0;
        playerResponses = 0;
        textWatchGo.setText("WATCH!");
        playSequence = true;
    }

    public void sequenceFinished() {
        playSequence = false;
        //make sure all the buttons are made visible
//        button1.setVisibility(View.VISIBLE);
//        button2.setVisibility(View.VISIBLE);
//        button3.setVisibility(View.VISIBLE);
//        button4.setVisibility(View.VISIBLE);
        textWatchGo.setText("GO!");
        isResponding = true;
    }

    public void checkElement(int thisElement) {
        if (isResponding) {
            playerResponses++;
            if (sequenceToCopy[playerResponses - 1] == thisElement) {
                //correct
                playerScore = playerScore + ((thisElement + 1) * 2);
                textScore.setText("Score: " + playerScore);
                if (playerResponses == difficultyLevel) {
                    //got the whole sequence, dont check anymore
                    isResponding = false;
                    //raise the difficulty
                    difficultyLevel++;
                    //play another sequence
                    playASequence();
                }
            } else {
                //wrong
                textWatchGo.setText("FAILED!");
                //dont check element anymore
                isResponding = false;
                layout.setBackgroundColor(Color.RED);

                //for the high score
                if(playerScore>hiScore){
                    hiScore = playerScore;
                    editor.putInt(intName, hiScore);
                    editor.commit();
                    //make a toast
                    Toast.makeText(getApplicationContext(), "New Hi-Score", Toast.LENGTH_LONG).show();
                }

            }

        }
    }
}