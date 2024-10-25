package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.R;
import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.models.AirBalloon;

public class AirBalloonObject extends GameObject{
    float startX, startY;
    float offsetX, offsetY;
    private int collectedCoins = 0;
    private int hp = 5;
    float newX;
    AirBalloon airBalloonInfo;

    // Добавляем переменную для отслеживания состояния перемещения
    private boolean isDragging = false;

    float previousX = 0;

    float speed = 0.5f; //Скорость перемещения шарика.


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
//        calculateNewPosition();

        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);
        canvas.drawBitmap(image, xPosition, yPosition, null);
    }

    private void calculateNewPosition() {
        xPosition +=1 ;
    }

    //Тут логика с фиксированной скоростью, она не оч. Возможно стоит доработать хз

//    public boolean onTouch(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // Запоминаем начальную позицию пальца
//                startX = event.getRawX();
//                offsetX = xPosition - startX;
//                previousX = startX; // Сохраняем начальную позицию как предыдущую
//                isDragging = true;   // Пользователь начал движение
//                System.out.println("Нажатие");
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if (isDragging) {
//                    float currentX = event.getRawX();
//                    float deltaX = currentX - previousX; // Вычисляем изменение X
//                    previousX = currentX; // Обновляем предыдущую позицию
//
//                    // Устанавливаем заранее определенную скорость
//                    float newX = xPosition + (deltaX * speed); // Обновляем позицию шарика
//
//                    // Обновляем позицию шарика с учетом ограничений экрана
//                    if (newX >= 0 && newX <= (displayMetrics.widthPixels - width)) {
//                        xPosition = (int) newX;
//
//                        // Выводим направление движения
//                        if (deltaX > 0) {
//                            System.out.println("Перемещение вправо");
//                        } else if (deltaX < 0) {
//                            System.out.println("Перемещение влево");
//                        }
//                    }
//                }
//                break;
//
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP: // Обработка отпускания пальца
//                // Пользователь отпустил палец, прекращаем движение
//                isDragging = false;
//                System.out.println("Отпустил");
//                break;
//        }
//        return true;
//    }
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

    public void removeHp() {
        hp--;
    }

    public int getHp() {
        return hp;
    }
}
