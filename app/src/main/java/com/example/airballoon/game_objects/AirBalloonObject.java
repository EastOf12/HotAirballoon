package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.models.AirBalloon;

public class AirBalloonObject extends GameObject{
    float startX, startY;
    float offsetX, offsetY;
    private int collectedCoins = 0;
    private int hp = 1;
    private final int maxXp = 1;
    float newX;

    public AirBalloonObject(Activity activity, DisplayMetrics displayMetrics, Bitmap image) {
        super(activity, displayMetrics);

        setPercentage(0.15);
        this.image = image;
        calculateSize();
        calculateStartPosition();
        createRect();
    }

    @Override
    protected void calculateStartPosition() {
        xPosition = (int) (displayMetrics.widthPixels * 0.4);
        yPosition = (int) (displayMetrics.heightPixels * 0.7);
    }

    @Override
    public void draw(Canvas canvas) {
        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);
        canvas.drawBitmap(image, xPosition, yPosition, null);
    }

    public boolean onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Запоминаем начальную позицию пальца
                startX = event.getRawX();
                startY = event.getRawY();
                offsetX = xPosition - startX;
                offsetY = yPosition - startY;
                break;
            case MotionEvent.ACTION_MOVE:
                // Рассчитываем новую позицию шарика в соответствии с перемещением пальца
                newX = event.getRawX() + offsetX;

                System.out.println("newX " + newX);
                System.out.println("event.getRawX() " + event.getRawX());


                // Обновляем позицию шарика с учетом ограничений экрана
                if (newX >= 0 && newX <= (displayMetrics.widthPixels - width)) {
                    xPosition = (int) newX;
                }
                break;
        }

        return true;
    }

    public Rect getRect() {
        return rect;
    }

    public void addCollectedCoins() {
        collectedCoins++;
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }
    public void resetCoins() {
        collectedCoins = 0;
    }

    public void removeHp() {
        hp--;
    }

    public int getHp() {
        return hp;
    }

    public void restartAirBalloon() {
        hp = maxXp;
        calculateStartPosition();
    }
}
