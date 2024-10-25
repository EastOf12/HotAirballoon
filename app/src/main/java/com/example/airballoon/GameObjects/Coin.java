package com.example.airballoon.GameObjects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.R;

import java.util.Random;

public class Coin extends GameObject{
    Random random = new Random();
    AirBalloon airBalloon;

    public Coin(Activity activity, DisplayMetrics displayMetrics, AirBalloon airBalloon) {
        super(activity, displayMetrics);

        this.airBalloon = airBalloon;
        setPercentage(0.08);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.coin);
        calculateSize();
        calculateStartPosition();
        createRect();
    }
    @Override
    protected void calculateStartPosition() {
        xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        yPosition = -50;
    }

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        }

        yPosition += GamePlayManager.speed;
    }

    @Override
    public void draw(Canvas canvas) {
        calculateNewPosition(canvas);
        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);

        canvas.drawBitmap(image, xPosition, yPosition, null);
    }

    public boolean checkCollisionAirBalloon() {
        boolean result = airBalloon.getRect().intersect(rect);

        if (result) {
            airBalloon.addCollectedCoins();
        }

        return result;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
}
