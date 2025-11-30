package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import com.example.airballoon.R;
import com.example.airballoon.managers.GamePlayManager;

import java.util.Objects;
import java.util.Random;

public class Magnet extends GameObject {
    Random random = new Random();
    AirBalloonObject airBalloon;
    private boolean needDraw;

    public Magnet(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        super(activity, displayMetrics);
        this.airBalloon = airBalloon;
        needDraw = true;
        setPercentage(0.12);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.magnet);
        calculateSize();
        calculateStartPosition();
        createRect();
    }

    @Override
    public void calculateStartPosition() {
        xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        yPosition = -50;
        needDraw = true;
    }

    @Override
    protected void draw(Canvas canvas) {
        if (needDraw) {
            calculateNewPosition(canvas);
            rect.left = xPosition;
            rect.top = yPosition;
            rect.right = (int) (xPosition + width);
            rect.bottom = (int) (yPosition + height);

            canvas.drawBitmap(image, xPosition, yPosition, null);
        }
    }

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        }

        yPosition += GamePlayManager.speed;
    }

//    public boolean checkCollisionAirBalloon() {
//
//        boolean result = airBalloon.getRect().intersect(rect);
//
//        if (result) {
//            airBalloon.addMagnet();
//        }
//
//        return result;
//    }

    public boolean checkCollisionAirBalloon() {
        boolean resultCenter = airBalloon.getRects().get(0).intersect(rect);
        boolean resultUp = airBalloon.getRects().get(1).intersect(rect);
        boolean resultBottom = airBalloon.getRects().get(2).intersect(rect);
        boolean res = false;

        if (resultCenter || resultUp || resultBottom) {
            airBalloon.addMagnet();
            res = true;
        }

        return res;
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Magnet magnet = (Magnet) o;
        return needDraw == magnet.needDraw && Objects.equals(random, magnet.random) && Objects.equals(airBalloon, magnet.airBalloon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(random, airBalloon, needDraw);
    }
}
