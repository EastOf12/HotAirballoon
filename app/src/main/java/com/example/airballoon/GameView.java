package com.example.airballoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.game_objects.BackGround;
import com.example.airballoon.game_objects.GearWheel;
import com.example.airballoon.managers.ManagerFPS;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;

import java.io.File;

@SuppressLint("ViewConstructor")
class GameView extends SurfaceView implements Runnable {
    private final SurfaceHolder surfaceHolder;
    Activity activity;
    DisplayMetrics displayMetrics;
    ManagerFPS managerFPS;
    GamePlayManager gamePlayManager;
    private volatile boolean running = false;
    private boolean isPaused = false;
    int startSpeed = 15;

    //Временный порядок
    User user;

    boolean needSave = true;


    @SuppressLint("UseCompatLoadingForDrawables")
    public GameView(Activity activity) {
        super(activity);
        this.activity = activity;

        //Получаем пользователя.
        user = SaveManager.readFromFile(activity);
        surfaceHolder = getHolder();

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        managerFPS = new ManagerFPS();
        gamePlayManager = new GamePlayManager(activity, displayMetrics, user);
    }

    @Override
    public void run() {
        // Создаем объекты
        BackGround backGround = new BackGround(activity, displayMetrics, R.drawable.game_bg);

        //Убрать в менеджер
        GearWheel gearWheel = new GearWheel(activity, displayMetrics);
        gamePlayManager.startMusic();

        while (running) {
            if (managerFPS.lockFPS()) {
                managerFPS.printFPS();

                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    synchronized (getHolder()) {

                        backGround.drawBackgroundImage(canvas);
                        gamePlayManager.drawAirBalloon(canvas);
                        gamePlayManager.drawCoins(canvas);
                        gamePlayManager.drawThorn(canvas);
                        gamePlayManager.drawCountCoins(canvas, displayMetrics);
                        gamePlayManager.drawHp(canvas, displayMetrics);
                        gamePlayManager.drawDistance(canvas, displayMetrics);

                        if(isPaused) {
                            gamePlayManager.drawGamePlayMenu(canvas);
                        } else {
                            //Увеличиваем скорость игры
                            gamePlayManager.speedUp();
                        }

                        gearWheel.drawGearWheel(canvas);

                        //Проверяем количество здоровья
                        if(gamePlayManager.getHpAirBalloon() <= 0) {
                            GamePlayManager.speed = 0;

                            if(needSave) {
                                //Сохраняем прогресс в файл.
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                SaveManager.save(activity, user);
                                needSave = false;
                            }

                            gamePlayManager.drawMenuEnd(canvas);
                        }
                    }

                    getHolder().unlockCanvasAndPost(canvas);
                }

                // Обработка касаний
                setOnTouchListener(new View.OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(gearWheel.onTouch(event) && !isPaused && gamePlayManager.getHpAirBalloon() > 0) {
                            switchGameStatus();
                        }

                        //Обрабатываем нажатия в меню.
                        if(gamePlayManager.getGamePlayMenu().onTouch(event) == MenuActions.RESUME) {
                            switchGameStatus();
                        } else if((gamePlayManager.getHpAirBalloon() <= 0 || isPaused) && gamePlayManager.getGamePlayMenu().onTouch(event) == MenuActions.EXIT) {
                            //Останавливаем поток
                            running = false;

                            //Создаем новую активность.
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);

                            // Завершить текущую активность
                            activity.finish();
                        } else if(gamePlayManager.getHpAirBalloon() <= 0 && gamePlayManager.
                                getGamePlayMenu().onTouch(event) == MenuActions.MARKETING) {
                            // Отображаем рекламу
                            running = false;

                            //Создаем активность с рекламой
                            Intent intent = new Intent(activity, RewardedAdActivity.class);
                            activity.startActivity(intent);
                        }

                        if(!isPaused && gamePlayManager.getHpAirBalloon()> 0) {
                            return gamePlayManager.onTouchAirBalloon(event);
                        } else {
                           return true;
                        }
                    }
                });
            }
        }

        gamePlayManager.releaseMusic();
    }

    public void start() {
        running = true;
        GamePlayManager.speed = startSpeed;
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public void switchGameStatus() {
        isPaused = !isPaused;

        if(isPaused) {
            GamePlayManager.speed = 0;
        } else {
            GamePlayManager.speed = 15;
        }
    }
}