package com.example.fallinghearts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Hands {
    private Bitmap bitmap;
    private float x, y; // Position of the hands
    private int width; // Width of the hands
    private int height; // Height of the hands

    public Hands(Context context, float x, float y) {
        this.x = x;
        this.y = y;

        // Load the hands image bitmap
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.hands);

        // Set width and height of the hands
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }

    // Call this method to update the position of the hands based on touch events
    public void updatePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Use this method to get the current y position of the hands
    public float getY() {
        return this.y;
    }

    // To get the width of the hands for positioning purposes
    public int getWidth() {
        return this.width;
    }

    // To get the height of the hands for positioning and drawing purposes
    public int getHeight() {
        return this.height;
    }

    // Draw the hands on the canvas
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    // Implement collision detection method to check collision with hearts and devils
    public boolean collidesWith(float objectX, float objectY, int objectWidth, int objectHeight) {
        // Create RectF for hands and object
        RectF handsRect = new RectF(x, y, x + width, y + height);
        RectF objectRect = new RectF(objectX, objectY, objectX + objectWidth, objectY + objectHeight);

        // Check if the handsRect intersects with the objectRect
        return handsRect.intersect(objectRect);
    }

    // Add a method to get the bounding box of the hands
    public RectF getBoundingBox() {
        return new RectF(x, y, x + width, y + height);
    }
}
