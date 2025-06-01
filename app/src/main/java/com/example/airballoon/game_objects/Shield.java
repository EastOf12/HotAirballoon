package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import com.example.airballoon.R;
import com.example.airballoon.managers.GamePlayManager;

import java.util.Objects;
import java.util.Random;

public class Shield extends GameObject{
    Random random = new Random();
    AirBalloonObject airBalloon;

    private boolean needDraw;

    public Shield(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        super(activity, displayMetrics);

        this.airBalloon = airBalloon;
        needDraw = true;
        setPercentage(0.12);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.shield);
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

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        }

        yPosition += GamePlayManager.speed;
    }

    public boolean checkCollisionAirBalloon() {

        boolean result = airBalloon.getRect().intersect(rect);

        if (result) {
            airBalloon.addShield();
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shield shield = (Shield) o;
        return needDraw == shield.needDraw && Objects.equals(random, shield.random) && Objects.equals(airBalloon, shield.airBalloon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(random, airBalloon, needDraw);
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

}
