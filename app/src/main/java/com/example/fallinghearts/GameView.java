package com.example.fallinghearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
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
    private int points = 1;
    private Paint textPaint; // Paint object for drawing text

    private boolean gameStarted = false;

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
            // Double the number of hearts to spawn by adjusting the range
            int heartsToSpawn = 2 + rand.nextInt(3); // Previously was 1 + rand.nextInt(2)
            for (int i = 0; i < heartsToSpawn; i++) {
                // Assuming heart width after scaling is about 50px, adjust as necessary
                float x = rand.nextFloat() * (getWidth() - 50) + 25; // Add padding to avoid spawning at the very edge
                heartsList.add(new Hearts(getContext(), x, 0));
            }

            // Increase the chance of spawning devils
            // By removing the if condition, you effectively spawn devils every time entities are spawned
            // If you still want some variability, but with more devils, consider adjusting the condition
            int devilsToSpawn = 1 + rand.nextInt(2); // Spawn 1 to 2 devils every time
            for (int i = 0; i < devilsToSpawn; i++) {
                // Assuming devil width after scaling is about 50px, adjust as necessary
                float x = rand.nextFloat() * (getWidth() - 50) + 25; // Add padding to avoid spawning at the very edge
                devilsList.add(new Devil(getContext(), x, 0));
            }

            lastSpawnTime = currentTime;
            gameStarted = true; // Set the game as started after the first spawn cycle
        }
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // Optionally log the exception
            }
        }
        thread = null;
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

    private void showLoseMessage(Canvas canvas) {
        canvas.drawColor(android.graphics.Color.WHITE);
        String loseMessage = "You Lose!";
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        canvas.drawText(loseMessage, centerX, centerY, textPaint);
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

                            // Update and draw each heart and devil
                            for (Hearts heart : heartsList) {
                                heart.update();
                                // Check collision with hands here if needed
                                if (hands.collidesWith(heart.getX(), heart.getY(), heart.getWidth(), heart.getHeight())) {
                                    points += heart.getPoints(); // Assume getPoints() exists and updates the score
                                    // Remove heart from list or flag for removal
                                }
                                heart.draw(canvas);
                            }
                            heartsList.removeIf(heart -> heart.getY() > getHeight() || hands.collidesWith(heart.getX(), heart.getY(), heart.getWidth(), heart.getHeight()));

                            for (Devil devil : devilsList) {
                                devil.update();
                                // Check collision with hands here
                                if (hands.collidesWith(devil.getX(), devil.getY(), devil.getWidth(), devil.getHeight())) {
                                    points -= 20; // Deduct 20 points for each collision with a devil
                                    // Remove devil from list or flag for removal to avoid multiple deductions for the same devil
                                }
                                devil.draw(canvas);
                            }
                            devilsList.removeIf(devil -> devil.getY() > getHeight() || hands.collidesWith(devil.getX(), devil.getY(), devil.getWidth(), devil.getHeight()));


                            // Update and draw hands
                            hands.draw(canvas);

                            // Draw the points counter at the top
                            canvas.drawText("Points: " + points, 10, 50, textPaint);

                            spawnEntities(); // Continue to spawn entities as necessary
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
