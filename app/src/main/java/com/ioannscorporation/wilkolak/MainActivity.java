package com.ioannscorporation.wilkolak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    ImageButton startButton, choiceButton;

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
        });
        choiceButton.setOnClickListener(v -> {
            //start choice level activity
        });
    }
}