package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import com.example.airballoon.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ShieldIcon extends GameObject {
    private boolean isShieldActive = false;
    private long shieldDuration; // Длительность щита в миллисекундах
    private long timeLeft; // Оставшееся время
    private Paint paint; // Для отрисовки анимации
    private CountDownTimer countDownTimer;
    private float xPos = 0;
    private float yPos = 0;
    int rotationSpeed = 4;
    List<Integer> angles;

    public ShieldIcon(Activity activity, DisplayMetrics displayMetrics) {
        super(activity, displayMetrics);
        setPercentage(0.07);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.shield);
        calculateSize();
        calculateStartPosition();
        createRect();

        paint = new Paint();
        paint.setColor(Color.YELLOW); // Цвет анимации
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8); // Толщина линии

        //Заполняем начальные углы расположения щитов
        angles = new ArrayList<>();
        angles.add(0);
        angles.add(90);
        angles.add(180);
        angles.add(270);
    }

    @Override
    protected void calculateStartPosition() {
        xPosition = (int) (displayMetrics.widthPixels * 0.05);
        yPosition = (int) (displayMetrics.heightPixels * 0.2);
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawBitmap(image, xPosition, yPosition, null);

        if (isShieldActive) {
            drawShieldAnimation(canvas);
        }
    }

    private void drawShieldAnimation(Canvas canvas) {
        // Определяем радиус щита
        float radius = Math.min(image.getWidth(), image.getHeight()) / 2;

        float offset = 20; // Отступ в пикселях

        // Центр круга
        float cx = xPosition + radius;
        float cy = yPosition + radius;

        // Новый радиус с учетом отступа
        float outerRadius = radius + offset;

        // Рассчитываем угол, на который нужно нарисовать часть круга
        float angle = (float) (360.0 * (timeLeft / (float) shieldDuration));

        // Рисуем круг с учетом нового радиуса
        canvas.drawArc(cx - outerRadius, cy - outerRadius, cx + outerRadius,
                cy + outerRadius, -90, angle, false, paint);
    }

    public void drawShieldAnimationAirballoon(Canvas canvas, Bitmap airballoon, int xNext, int yNext) {
        for (int i = 0; i < angles.size(); i++) {
            // Получаем размеры битмапа
            int airballoonWidth = airballoon.getWidth();
            int airballoonHeight = airballoon.getHeight();

            // Определяем радиусы для овала
            float radiusX = airballoonWidth * 0.8f; // Горизонтальный радиус
            float radiusY = airballoonHeight * 0.7f; // Вертикальный радиус
            float centerX = (float) (xNext + (airballoonWidth * 0.3)); // Центр по X
            float centerY = (float) (yNext + (airballoonHeight * 0.3)); // Центр по Y

            // Определение скорости вращения
            angles.set(i, angles.get(i) + rotationSpeed); // Увеличиваем угол для движения
            if (angles.get(i) >= 360) { // Если угол превышает 360 градусов, сбрасываем
                angles.set(i, angles.get(i) - 360) ;
            }

            // Рассчитываем позиции щита по овальной траектории
            xPos = centerX + (float) (radiusX * Math.cos(Math.toRadians(angles.get(i)))); // Новый X для овала
            yPos = centerY + (float) (radiusY * Math.sin(Math.toRadians(angles.get(i)))); // Новый Y для овала

            // Рисуем битмап щита
            canvas.drawBitmap(image, xPos, yPos, null);
        }
    }

    public void startShieldTimer(long duration) {
        this.shieldDuration = duration;
        this.timeLeft = duration;
        this.isShieldActive = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Создаем новый таймер в главном потоке
        countDownTimer = new CountDownTimer(duration, 100) { //Время обновления таймера в мс
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                // Вызывайте перерисовку здесь, если это необходимо
            }

            @Override
            public void onFinish() {
                isShieldActive = false;
                timeLeft = 0;
                // Вызывайте перерисовку здесь, если это необходимо
            }
        }.start();
    }
}
