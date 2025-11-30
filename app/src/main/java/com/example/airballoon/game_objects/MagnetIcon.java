package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;

import com.example.airballoon.R;

import java.util.ArrayList;
import java.util.List;

public class MagnetIcon extends GameObject{
    private boolean isMagnetActive = false;
    private long magnetDuration; // Длительность щита в миллисекундах
    private long timeLeft; // Оставшееся время
    private Paint paint; // Для отрисовки анимации
    private CountDownTimer countDownTimer;
    private float xPos = 0;
    private float yPos = 0;
    int rotationSpeed = 4;
    List<Integer> angles;
    public MagnetIcon(Activity activity, DisplayMetrics displayMetrics) {
        super(activity, displayMetrics);
        setPercentage(0.07);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.magnet);
        calculateSize();
        calculateStartPosition();
        createRect();

        paint = new Paint();
        paint.setColor(Color.YELLOW); // Цвет анимации
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8); // Толщина линии
    }

    @Override
    protected void calculateStartPosition() {
        xPosition = (int) (displayMetrics.widthPixels * 0.05);
        yPosition = (int) (displayMetrics.heightPixels * 0.28);
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.drawBitmap(image, xPosition, yPosition, null);

        if (isMagnetActive) {
            drawMagnetAnimation(canvas);
        }
    }

    private void drawMagnetAnimation(Canvas canvas) {
        // Определяем радиус щита
        float radius = (float) Math.min(image.getWidth(), image.getHeight()) / 2;

        float offset = 20; // Отступ в пикселях

        // Центр круга
        float cx = xPosition + radius;
        float cy = yPosition + radius;

        // Новый радиус с учетом отступа
        float outerRadius = radius + offset;

        // Рассчитываем угол, на который нужно нарисовать часть круга
        float angle = (float) (360.0 * (timeLeft / (float) magnetDuration));

        // Рисуем круг с учетом нового радиуса
        canvas.drawArc(cx - outerRadius, cy - outerRadius, cx + outerRadius,
                cy + outerRadius, -90, angle, false, paint);
    }

    public void startMagnetTimer(long duration) {
        this.magnetDuration = duration;
        this.timeLeft = duration;
        this.isMagnetActive = true;

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
                isMagnetActive = false;
                timeLeft = 0;
                // Вызывайте перерисовку здесь, если это необходимо
            }
        }.start();
    }
}
