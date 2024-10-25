package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.example.airballoon.MainActivity;
import com.example.airballoon.RewardedAdActivity;
import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.SaveManager;

@SuppressLint("ViewConstructor")
public class Level1 extends BaseLevel implements Runnable{
    public Level1(Activity activity) {
        super(activity);
    }


    //Основной цикл игры.
    @Override
    public void run() {
        gamePlayManager.startMusic();

        while (running) {
            if (managerFPS.lockFPS()) {
                managerFPS.printFPS();

                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    synchronized (getHolder()) {

                        gamePlayManager.drawBackGround(canvas);
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

                        gamePlayManager.drawGearWheel(canvas);

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
                        if(gamePlayManager.onTouchGearWheel(event) && !isPaused && gamePlayManager.getHpAirBalloon() > 0) {
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
}
