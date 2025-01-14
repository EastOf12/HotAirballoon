package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.example.airballoon.MainActivity;
import com.example.airballoon.RewardedAdActivity;
import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.managers.GamePlayStatus;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.ObjectsGeneration;
import com.example.airballoon.managers.SaveManager;

@SuppressLint("ViewConstructor")
public class Level2 extends BaseLevel implements Runnable{
    public Level2(Activity activity) {
        super(activity);

        //Уникальные настройки для уровня
        gamePlayManager.setEndDistance(200); //Финальная дистанация
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

                        //Отрисовка основных объектов
                        gamePlayManager.drawBackGround(canvas);
                        gamePlayManager.drawAirBalloon(canvas);
                        gamePlayManager.drawCoins(canvas);
                        gamePlayManager.drawThorn(canvas);
                        gamePlayManager.drawCountCoins(canvas, displayMetrics);
                        gamePlayManager.drawHp(canvas, displayMetrics);
                        gamePlayManager.drawDistance(canvas, displayMetrics);
                        gamePlayManager.drawBackGround(canvas); //Добавить фон
                        gamePlayManager.drawAirBalloon(canvas); //Добавить шарик

                        gamePlayManager.startObjectsGeneration(canvas); //Добавить генерацию игровых объектов

//                        gamePlayManager.drawCoins(canvas); Старый вариант генераций
//                        gamePlayManager.drawThorn(canvas);


                        gamePlayManager.drawCountCoins(canvas, displayMetrics); //Отрисовать количество монет
                        gamePlayManager.drawHp(canvas, displayMetrics); //Отрисовать количество здоровья
                        gamePlayManager.drawDistance(canvas, displayMetrics); //Отрисовать дистанцию

                        //Управление состояниями игры
                        if(gamePlayStatus == GamePlayStatus.PAUSE) {
                            gamePlayManager.drawGamePlayMenu(canvas);
                        } else if (gamePlayManager.levelEnd()) {
                            switchGameStatus(GamePlayStatus.END);
                            gamePlayManager.drawLevelEnd(canvas);
                        } else {
                            gamePlayManager.speedUp();
                        }

                        gamePlayManager.drawGearWheel(canvas); //Отрисовать кнопку настроек

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

                            gamePlayManager.drawMenuEndLose(canvas);
                        }
                    }

                    getHolder().unlockCanvasAndPost(canvas);
                }

                // Обработка касаний
                setOnTouchListener(new OnTouchListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(gamePlayManager.onTouchGearWheel(event) && gamePlayStatus != GamePlayStatus.PAUSE && gamePlayManager.getHpAirBalloon() > 0) {
                            switchGameStatus(GamePlayStatus.PlAY);
                        }

                        //Обрабатываем нажатия в меню.
                        if(gamePlayManager.getGamePlayMenu().onTouch(event) == MenuActions.RESUME) {
                            switchGameStatus(GamePlayStatus.PlAY);
                        } else if((gamePlayManager.getHpAirBalloon() <= 0 || gamePlayStatus == GamePlayStatus.PAUSE) && gamePlayManager.getGamePlayMenu().onTouch(event) == MenuActions.EXIT) {
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

                        if(gamePlayStatus != GamePlayStatus.PAUSE && gamePlayManager.getHpAirBalloon()> 0) {
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
