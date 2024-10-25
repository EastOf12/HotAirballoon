package com.example.airballoon.GameObjects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.R;

public class AirBalloon extends GameObject{
    float startX, startY;
    float offsetX, offsetY;
    private int collectedCoins = 0;
    private int hp = 5;
    float newX;

    public AirBalloon(Activity activity, DisplayMetrics displayMetrics) {
        super(activity, displayMetrics);

        setPercentage(0.15);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.air_balloon);
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

    public void removeHp() {
        hp--;
    }

    public int getHp() {
        return hp;
    }
}
