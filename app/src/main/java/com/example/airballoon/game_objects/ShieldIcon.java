package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import com.example.airballoon.R;

public class ShieldIcon extends GameObject {
    private boolean isShieldActive = false;
    private long shieldDuration; // Длительность щита в миллисекундах
    private long timeLeft; // Оставшееся время
    private Paint paint; // Для отрисовки анимации
    private Handler handler; // Обработчик для работы с UI потоком

    private CountDownTimer countDownTimer;

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
        handler = new Handler(Looper.getMainLooper()); // Создаем обработчик с привязкой к основному потоку


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
