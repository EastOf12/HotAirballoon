package com.example.airballoon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class GameViewOld extends SurfaceView implements Runnable {
    private final SurfaceHolder surfaceHolder;
    private Thread gameThread;
    private volatile boolean running = false;

    private Bitmap backgroundImage;
    private Bitmap coinImage;
    private int backgroundY = 0;

    @SuppressLint("UseCompatLoadingForDrawables")
    public GameViewOld(Context context) {
        super(context);
        surfaceHolder = getHolder();

        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.game_bg);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, 5000, 5000, true);


        coinImage = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        coinImage = Bitmap.createScaledBitmap(coinImage, 70, 70, true);
    }

    @Override
    public void run() {
        //Замеры ФПС
        long beginTime;
        long endTime;
        long elapsedTime;

        //Метод в рамках которого запущен основной цикл игры.
        while (running) {
            beginTime = System.nanoTime();

            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
//                System.out.println(canvas + " канвас сразу после получения");

                // Draw the background image at the current position
                canvas.drawBitmap(backgroundImage, 0, 0, null);

                //Рисуем саму монетку
                canvas.drawBitmap(coinImage, 0, backgroundY, null);

                // Update the position of the background image
                // Speed at which the image moves
                int backgroundSpeed = 25;
                backgroundY += backgroundSpeed;

                // If the image is out of bounds, reset its position to the top of the screen
                if (backgroundY >= canvas.getHeight()) {
                    backgroundY = 0;
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }

            endTime = System.nanoTime();
            elapsedTime = endTime - beginTime;
            // Вычисляем скорость прохождения цикла в кадрах в секунду
            int framesPerSecond = (int) (1e9 / elapsedTime); // 1e9 - это наносекунда в секунде

            System.out.println(framesPerSecond);
        }
    }

    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}