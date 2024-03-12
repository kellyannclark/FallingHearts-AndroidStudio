package com.example.fallinghearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import android.util.Log;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private Hands hands; // Represents the player's hands
    private List<Hearts> heartsList;
    private List<Devil> devilsList;
    private long lastSpawnTime;
    private static final long SPAWN_INTERVAL = 2000; // 2 seconds between spawns
    private int points = 0;
    private Paint textPaint; // Paint object for drawing text

    // Add a method to show the lose message
    private void showLoseMessage(Canvas canvas) {
        String loseMessage = "You Lose!";
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Draw the lose message at the center of the screen
        canvas.drawText(loseMessage, centerX, centerY, textPaint);
    }

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        hands = new Hands(context, 100, 100); // Temporary initialization
        heartsList = new ArrayList<>();
        devilsList = new ArrayList<>();
        lastSpawnTime = System.currentTimeMillis(); // Initialize spawn timer

        // Initialize textPaint object
        textPaint = new Paint();
        textPaint.setColor(android.graphics.Color.BLACK);
        textPaint.setTextSize(40); // Adjust text size as needed
    }

    public void pause() {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        thread = new GameThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    private void spawnEntities() {
        Random rand = new Random();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            int heartsToSpawn = 1 + rand.nextInt(2); // Adjust the number of hearts to spawn
            for (int i = 0; i < heartsToSpawn; i++) {
                float x = rand.nextFloat() * (getWidth() - 100);
                heartsList.add(new Hearts(getContext(), x, 0));
            }

            // Increase the frequency of devil spawns
            if (rand.nextInt(2) == 0) { // 1/2 chance instead of 1/6
                float x = rand.nextFloat() * (getWidth() - 100);
                devilsList.add(new Devil(getContext(), x, 0));
            }

            lastSpawnTime = currentTime;
        }

        // Check if points hit zero or less
        if (points <= 0) {
            // Game over, show lose message
            Canvas canvas = getHolder().lockCanvas();
            try {
                if (canvas != null) {
                    canvas.drawColor(android.graphics.Color.WHITE);
                    showLoseMessage(canvas);
                }
            } finally {
                if (canvas != null) {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }

            // End the game
            pause();
            return; // Exit the method to prevent further entity spawning
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GameView", "surfaceCreated");
        if (thread.getState() == Thread.State.NEW) {
            thread.setRunning(true);
            thread.start();
        }
    }



    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        float handsY = height - hands.getHeight();
        hands.updatePosition(width / 2f, handsY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false); // Signal the thread to stop
        while (retry) {
            try {
                thread.join(); // Wait for the thread to finish
                retry = false;
            } catch (InterruptedException e) {
                // Optionally log the exception
            }
        }
        thread = null; // Help with garbage collection
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                hands.updatePosition(x - hands.getWidth() / 2f, hands.getY());
                break;
        }
        return true;
    }

    class GameThread extends Thread {
        private final SurfaceHolder surfaceHolder;
        private boolean running = false;
        private GameView gameView;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            while (running) {
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        if (canvas != null) {
                            canvas.drawColor(android.graphics.Color.WHITE); // Clear the canvas

                            // Now draw each heart and devil
                            for (Hearts heart : heartsList) {
                                heart.draw(canvas);
                            }
                            for (Devil devil : devilsList) {
                                devil.draw(canvas);
                            }

                            gameView.spawnEntities(); // Continue to spawn entities as necessary
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

    }
}
