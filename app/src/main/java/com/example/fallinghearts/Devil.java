package com.example.fallinghearts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Devil {
    private final Bitmap bitmap;
    private final float x;
    private float y; // This changes as the devil moves, so it can't be final
    private final float velocity = 50; // Assuming this doesn't change
    private final RectF rect; // The RectF will be modified, but the reference doesn't change
    private final int points = 10; // Assuming this is constant

    public Devil(Context context, float x, float y) {
        this.x = x;
        this.y = y;

        // Load and scale the devil image bitmap directly
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.devil);
        float scaleFactor = 1.0f; // Example scale factor, adjust based on your needs
        int width = tempBitmap.getWidth();
        int height = tempBitmap.getHeight();
        this.bitmap = Bitmap.createScaledBitmap(tempBitmap, (int)(width * scaleFactor), (int)(height * scaleFactor), false);

        // Initialize the rectangle for the devil
        this.rect = new RectF(this.x, this.y, this.x + this.bitmap.getWidth(), this.y + this.bitmap.getHeight());
    }

    public void update() {
        // Move the devil down
        this.y += this.velocity;
        // Update the rectangle's position for collision detection or other purposes
        this.rect.set(this.x, this.y, this.x + this.bitmap.getWidth(), this.y + this.bitmap.getHeight());
    }

    public void draw(Canvas canvas) {
        // Draw the devil bitmap
        canvas.drawBitmap(this.bitmap, this.x, this.y, null);
    }

    // Getter for position, size, and rect for collision detection
    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getWidth() {
        return this.bitmap.getWidth();
    }

    public int getHeight() {
        return this.bitmap.getHeight();
    }

    public RectF getRect() {
        return this.rect;
    }

    // Getter for points
    public int getPoints() {
        return this.points;
    }
}
