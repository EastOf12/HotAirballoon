package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.R;

public class GearWheel {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap gearWheelImage;
    int xPosition;
    int yPosition;
    double percentage = 0.13; // Размер изображения относительно экрана
    double width;
    double height;

    public GearWheel(Activity activity, DisplayMetrics displayMetrics) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        gearWheelImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.gear_wheel);
        calculateSize();
        calculateStartPosition();
    }

    private void calculateSize() {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) gearWheelImage.getWidth() / gearWheelImage.getHeight();
        height = width / proportion;
        gearWheelImage = Bitmap.createScaledBitmap(gearWheelImage
                , (int) width, (int) height, true);
    }

    private void calculateStartPosition() {
        xPosition = (int) (displayMetrics.widthPixels * 0.02);
        yPosition = (int) (displayMetrics.heightPixels * 0.10);
    }

    public void drawGearWheel(Canvas canvas) {
        canvas.drawBitmap(gearWheelImage, xPosition, yPosition, null);
    } //Рисуем шестеренку.

    public boolean onTouch(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchX >= xPosition && touchX < (xPosition + width) &&
                    touchY >= yPosition && touchY < (yPosition + height)) {
                return true;
            }
        }

        return false;
    }
}
