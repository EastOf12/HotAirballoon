package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.R;

import java.util.Random;

public class Coin extends GameObject{
    Random random = new Random();
    AirBalloonObject airBalloon;

    private boolean needDraw;

    public Coin(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        super(activity, displayMetrics);

        this.airBalloon = airBalloon;
        needDraw = true;
        setPercentage(0.08);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.coin);
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

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        }

        yPosition += GamePlayManager.speed;
    }


    @Override
    public void draw(Canvas canvas) {

        if(needDraw) {
            calculateNewPosition(canvas);
            rect.left = xPosition;
            rect.top = yPosition;
            rect.right = (int) (xPosition + width);
            rect.bottom = (int) (yPosition + height);

            canvas.drawBitmap(image, xPosition, yPosition, null);
        }
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

    public boolean isNeedDraw() {
        return needDraw;
    }
}
