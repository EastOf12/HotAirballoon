package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import com.example.airballoon.R;
import com.example.airballoon.managers.GamePlayManager;

import java.util.Objects;
import java.util.Random;

public class Bird extends GameObject{
    AirBalloonObject airBalloon;
    Random random = new Random();
    int birdSpeed = 8; //Скорость птицы
    private DirectionMovement directionMovement;

    private boolean needDraw;
    public Bird(Activity activity, DisplayMetrics displayMetrics,  AirBalloonObject airBalloon) {
        super(activity, displayMetrics);
        this.airBalloon = airBalloon;
        needDraw = true;
        directionMovement = DirectionMovement.LEFT;
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bird);

        setPercentage(0.16);

        calculateSize();
        calculateStartPosition();
        createRect();
    }

    @Override
    public void calculateStartPosition() {
        //Определяем направление движения и инвертируем изображение при необходимости
        DirectionMovement newDirection = calculateDirection();
        if(!directionMovement.equals(newDirection)) {
            directionMovement = newDirection;
            flipBitmapHorizontally();
        }

        xPosition = calculateStartXPosition();
        yPosition = -150;
        needDraw = true;
    } //Определяем параметры птицы при генерации

    @Override
    protected void draw(Canvas canvas) {
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
        if (yPosition >= canvas.getHeight() || checkCollision()) {
            yPosition = -150;
            xPosition = calculateStartXPosition();
            needDraw = !needDraw;
        }

        yPosition += (GamePlayManager.speed);
        calculateNewXPosition();
    }

    private int calculateStartXPosition() {
        int x;
        if(directionMovement.equals(DirectionMovement.LEFT)) {
            x = 10;
        } else {
            x = random.nextInt((int) (displayMetrics.widthPixels * 0.2)) + displayMetrics.widthPixels;
        }

        return x;
    }

    private void calculateNewXPosition() {
        if(directionMovement.equals(DirectionMovement.LEFT)) {
            xPosition += birdSpeed;
        } else {
            xPosition -= birdSpeed;
        }
    }

    public boolean checkCollision() {
        boolean result = airBalloon.getRect().intersect(rect);

        if (result) {
            airBalloon.removeHp();
        }

        return result;
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    private DirectionMovement calculateDirection() {
        if(random.nextInt(2) == 1) {
            return DirectionMovement.LEFT;
        } else {
            return DirectionMovement.RIGHT;
        }
    } //Определяем направление движения птицы

    private void flipBitmapHorizontally() {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
    } //Инверсируем изображение по горизонтали

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bird bird = (Bird) o;
        return needDraw == bird.needDraw && Objects.equals(airBalloon, bird.airBalloon) && Objects.equals(random, bird.random);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airBalloon, random, needDraw);
    }
}

enum DirectionMovement {
    LEFT,
    RIGHT
}
