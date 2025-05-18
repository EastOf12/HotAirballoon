package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.example.airballoon.GamePlayActivity;
import com.example.airballoon.MainActivity;
import com.example.airballoon.RewardedAdActivity;
import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.SaveManager;

@SuppressLint("ViewConstructor")
public class FreeLevel extends BaseLevel implements Runnable{
    public FreeLevel(Activity activity) {
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

                        gamePlayManager.drawBackGround(canvas); //Добавляем фон
                        gamePlayManager.drawAirBalloon(canvas); //Добавляем шарик
                        gamePlayManager.startObjectsGeneration(canvas); //Добавляем генерацию игровых объектов
                        gamePlayManager.drawCountCoins(canvas, displayMetrics); //Добавляем количество монет
                        gamePlayManager.drawHp(canvas, displayMetrics); //Добавляем количество здоровья
                        gamePlayManager.drawDistance(canvas, displayMetrics); //Добавляем дистанцию

                        if(isPaused) {
                            gamePlayManager.drawGamePlayMenu(canvas);
                        } else {
                            gamePlayManager.speedUp(); //Увеличиваем скорость игры
                        }

                        gamePlayManager.drawGearWheel(canvas); //Добавляем кнопку настроек

                        if(gamePlayManager.getHpAirBalloon() <= 0) { //Проверяем количество здоровья
                            GamePlayManager.speed = 0;

                            gamePlayManager.drawGameOver(canvas, displayMetrics); //Выводим сообщение о конце игры

                            if(needSave) {
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                SaveManager.save(activity, user); //Сохраняем прогресс в файл.
                                needSave = false;
                            }

                            gamePlayManager.drawMenuEnd(canvas);
                        }
                    }

                    getHolder().unlockCanvasAndPost(canvas);
                }

                setOnTouchListener(new OnTouchListener() { // Обрабатываем касания
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        if(gamePlayManager.onTouchGearWheel(event) && !isPaused && gamePlayManager.getHpAirBalloon() > 0) {
                            switchGameStatus();
                        }

                        if(gamePlayManager.getGamePlayMenu().onTouch(event, isPaused) == MenuActions.RESUME) { //Обрабатываем нажатия в меню.
                            switchGameStatus();
                        } else if((gamePlayManager.getHpAirBalloon() <= 0 || isPaused)
                                && gamePlayManager.getGamePlayMenu().onTouch(event, isPaused) == MenuActions.EXIT) {
                            running = false; //Останавливаем поток

                            //Создаем новую активность.
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);

                            // Завершить текущую активность
                            activity.finish();
                        } else if(gamePlayManager.getHpAirBalloon() <= 0 && gamePlayManager.
                                getGamePlayMenu().onTouch(event, isPaused) == MenuActions.RESTART) {

                            restartGame();
                        }

/*
                        else if(gamePlayManager.getHpAirBalloon() <= 0 && gamePlayManager.
                                getGamePlayMenu().onTouch(event) == MenuActions.MARKETING) {
                            running = false;

                            Intent intent = new Intent(activity, RewardedAdActivity.class); //Создаем активность с рекламой
                            activity.startActivity(intent);
                        } //Логика запуска рекламы
*/

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

    private void restartGame() {
        gamePlayManager.restartAirballoon();
        gamePlayManager.restartSpeed();
        gamePlayManager.restartDistance();
        gamePlayManager.restartCoins();
        gamePlayManager.restartGeneration();
        gamePlayManager.restartBackGround();
        needSave = true;

    } //Перезапуск уровня.
}
