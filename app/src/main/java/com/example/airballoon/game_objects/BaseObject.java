package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

public class BaseObject {
    private final double percentage;
    public Bitmap image;
    protected int xPosition;
    protected int yPosition;
    protected double width;
    protected double height;
    Activity activity;
    DisplayMetrics displayMetrics;

    public BaseObject(Activity activity, DisplayMetrics displayMetrics,
                      double percentage) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.percentage = percentage;
    }

    public void loadImage(Activity activity, int resourceId) {
        image = BitmapFactory.decodeResource(activity.getResources(), resourceId);
    }

    public void calculateSize(DisplayMetrics displayMetrics) {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) image.getWidth() / image.getHeight();
        height = width / proportion;
        image = Bitmap.createScaledBitmap(image
                , (int) width, (int) height, true);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, xPosition, yPosition,
                null);
    }

    public void setPositions(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
