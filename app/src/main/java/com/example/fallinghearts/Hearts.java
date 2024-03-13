package com.example.fallinghearts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Hearts {
    private float x, y; // Position of the heart
    private float velocity = 10; // Speed of the heart's descent
    private int width; // Width of the heart
    private int height; // Height of the heart
    private Bitmap bitmap; // Heart image bitmap
    private int points; // Points that the heart is worth

    public Hearts(Context context, float x, float y) {
        this.x = x;
        this.y = y;

        // Load the heart image bitmap and scale it down
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        int width = tempBitmap.getWidth() / 2; // Half the original width
        int height = tempBitmap.getHeight() / 2; // Half the original height
        this.bitmap = Bitmap.createScaledBitmap(tempBitmap, width, height, false);

        // Set width and height based on the scaled bitmap
        this.width = this.bitmap.getWidth();
        this.height = this.bitmap.getHeight();

        this.points = new java.util.Random().nextInt(10) + 1;
    }


    public void update() {
        // Move the heart down
        y += velocity;
    }

    // Getter for points
    public int getPoints() {
        return points;
    }

    public void draw(Canvas canvas) {
        // Draw the heart bitmap instead of a rect
        canvas.drawBitmap(bitmap, x, y, null);
    }

    // Getters for position and collision detection
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RectF getBoundingBox() {
        return new RectF(x, y, x + width, y + height);
    }
}
