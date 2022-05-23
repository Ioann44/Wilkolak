package com.ioannscorporation.wilkolak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    View startButton, choiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startGameButton);
        choiceButton = findViewById(R.id.choicePartButton);
        startButton.setOnClickListener(v -> {
            //start game activity
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            MainActivity.this.startActivity(intent);
            //gameMode is singleplayer
            UtilApp.gameModeIsMP = false;
            //player is wolf
            UtilApp.whoIAm = WhoIAm.wolf;
            //only 1 player
        });
        choiceButton.setOnClickListener(v -> {
            //start choice level activity
            //it start to multiplayer now
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            MainActivity.this.startActivity(intent);
            UtilApp.gameModeIsMP = true;
            UtilApp.whoIAm = WhoIAm.chicken; //only for now, in future you will be able to choice
//            UtilApp.numOfPlayers = 2; //only for now
        });
    }
}