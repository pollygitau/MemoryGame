package com.blaire.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;
    String dataName = "MyData";
    String intName = "MyInt";
    TextView txt1;
    int defaultInt = 0;
    int hiScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(dataName,MODE_PRIVATE);
        hiScore = prefs.getInt(intName,defaultInt);
        txt1 = (TextView) findViewById(R.id.textHiScore);


        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });
    }
    protected void onResume(){
        super.onResume();
        hiScore = prefs.getInt(intName,defaultInt);
        txt1.setText("Score: " + hiScore);
    }

}
