package com.example.airballoon.game_objects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import com.example.airballoon.R;
import com.example.airballoon.managers.GamePlayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Bird extends GameObject{
    private AirBalloonObject airBalloon;
    private Random random = new Random();
    private int birdSpeed = 7; //Скорость птицы
    private DirectionMovement directionMovement;
    private int frameCount = 0;
    private int framePoint = 5; //Количество кадров для смены спрайта
    private int imageCounter = 0; //Индекс текущего спрайта
    private List<Bitmap> imagesLeft; //Все спрайты птицы которые летят на право
    private List<Bitmap> imagesRight; //Все спрайты птицы которые летят на лево
    private Boolean needAnimation = true; //Останавливает анимацию птицы


    private boolean needDraw;
    public Bird(Activity activity, DisplayMetrics displayMetrics,  AirBalloonObject airBalloon) {
        super(activity, displayMetrics);
        this.airBalloon = airBalloon;
        needDraw = true;
        directionMovement = DirectionMovement.LEFT;
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.bird1);
        imagesLeft = new ArrayList<>();
        imagesRight = new ArrayList<>();

        setPercentage(0.17);
        calculateSize();

        calculateStartPosition();
        createRect();

        loadImages();
    }

    private void replaceImage() {
        frameCount++;

        if(frameCount >= framePoint) {

            if(directionMovement.equals(DirectionMovement.LEFT)) {
                image= imagesLeft.get(imageCounter);
            } else {
                image = imagesRight.get(imageCounter);
            }

            if(imageCounter == imagesLeft.size() - 1) {
                imageCounter = 0;
            } else {
                imageCounter++;
            }

            frameCount = 0;
        }
    } //Заменяем текущий спрайт следующим при необходимости

    private void loadImages() {
        for (int i = 1; i <= 12; i++) {
            String resourceName = "bird" + i;
            @SuppressLint("DiscouragedApi") int resourceId = activity.getResources().getIdentifier(resourceName, "drawable", activity.getPackageName());
            imagesLeft.add(BitmapFactory.decodeResource(activity.getResources(), resourceId));
            imagesLeft = calculateSizeImages();
        }

        flipBitmapHorizontally(); //Добавляем горизонтально отраженные спрайты птиц
    } //Загружаем все спрайты птицы

    private ArrayList<Bitmap> calculateSizeImages() {
        ArrayList<Bitmap> img = new ArrayList<>();

        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) image.getWidth() / image.getHeight();
        height = width / proportion;

        for(Bitmap im: imagesLeft) {
            img.add(Bitmap.createScaledBitmap(im
                    , (int) width, (int) height, true));
        }

        return img;
    } //Меняем размер изображения для всех спрайтов птиц

    @Override
    public void calculateStartPosition() {
        //Определяем направление движения и инвертируем изображение при необходимости
        DirectionMovement newDirection = calculateDirection();
        if(!directionMovement.equals(newDirection)) {
            directionMovement = newDirection;
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

            if(needAnimation) {
                replaceImage();
            }
        }
    }

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollision()) {
            yPosition = -150;
            xPosition = calculateStartXPosition();
            needDraw = !needDraw;
        }

        yPosition += (GamePlayManager.speed);

        if(GamePlayManager.speed != 0) {
            calculateNewXPosition();
        }
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

//    public boolean checkCollision() {
//        boolean result = airBalloon.getRect().intersect(rect);
//
//        if (result) {
//            airBalloon.removeHp();
//        }
//
//        return result;
//    }

    public boolean checkCollision() {
        boolean resultCenter = airBalloon.getRects().get(0).intersect(rect);
        boolean resultUp = airBalloon.getRects().get(1).intersect(rect);
        boolean resultBottom = airBalloon.getRects().get(2).intersect(rect);
        boolean res = false;

        if (resultCenter || resultUp || resultBottom) {
            airBalloon.removeHp();
            res = true;
        }

        return res;
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

        for(int i = 0; i < imagesLeft.size(); i++) {
            imagesRight.add(Bitmap.createBitmap(imagesLeft.get(i), 0, 0, imagesLeft.get(i).getWidth(),
                    imagesLeft.get(i).getHeight(), matrix, false));
        }
    } //Инверсируем изображение по горизонтали

    public void startAnimation() {
        needAnimation = true;
    } //Запускает анимацию птицы, если она была остановлена

    public void stopAnimation() {
        needAnimation = false;
    } //Останавливает анимацию птицы, если она была запущена

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
