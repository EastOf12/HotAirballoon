package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.R;

import java.util.Random;

public class Thorn {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap thornImage;
    Rect rect;
    int xPosition;
    int yPosition;
    double percentage = 0.2; // Размер изображения относительно экрана
    double width;
    double height;
    AirBalloonObject airBalloon;

    public Thorn(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.airBalloon = airBalloon;
        thornImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.thorn);
        calculateSize();
        calculateStartPosition();
        rect = new Rect(xPosition, yPosition, (int) (xPosition + width)
                , (int) (yPosition + height));
    }

    private void calculateSize() {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) thornImage.getWidth() / thornImage.getHeight();
        height = width / proportion;
        thornImage = Bitmap.createScaledBitmap(thornImage
                , (int) width, (int) height, true);
    }

    private void calculateStartPosition() {
        Random random = new Random();
        xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        yPosition = -50;
    }

    private void calculateNewPosition(Canvas canvas) {
        yPosition += GamePlayManager.speed;

        if (yPosition >= canvas.getHeight() || checkCollision()) {
            yPosition = -1500;
            Random random = new Random();
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        }

        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);
    }

    public void drawThorn(Canvas canvas) {
        calculateNewPosition(canvas);
        canvas.drawBitmap(thornImage, xPosition, yPosition, null);
    } //Рисуем монетку.

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }


    public boolean checkCollision() {
        boolean result = airBalloon.getRect().intersect(rect);

        if (result) {
            airBalloon.removeHp();
        }

        return result;
    }
}
