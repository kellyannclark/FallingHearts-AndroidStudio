package com.example.fallinghearts;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GameView gameView; // Declare the GameView object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the GameView object
        gameView = new GameView(this);
        setContentView(gameView);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game view if it's currently running
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the game view if it's not null
        if (gameView != null) {
            gameView.resume();
        }
    }
}
