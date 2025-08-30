package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.example.airballoon.MainActivity;
import com.example.airballoon.R;
import com.example.airballoon.RewardedAdActivity;
import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.SaveManager;

import java.util.HashMap;

@SuppressLint("ViewConstructor")
public class FreeLevel extends BaseLevel implements Runnable{
    private final int selectedLevel;
    private boolean levelCompleted = false;
    private static int stars = 0;
    private final HashMap<Integer, Integer> levelsFinishDistance = loadLevelFinishInfo();

    public FreeLevel(Activity activity, int selectedLevel) {
        super(activity);
        gamePlayManager = new GamePlayManager(activity, displayMetrics, user, selectedLevel);
        this.selectedLevel = selectedLevel;
    }

    //Основной цикл игры.
    @Override
    public void run() {
        gamePlayManager.startMusic();
        stars = 0;

        while (running) {
            if (managerFPS.lockFPS()) {
                managerFPS.printFPS();

                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    synchronized (getHolder()) {

                        gamePlayManager.drawBackGround(canvas); //Добавляем фон
                        gamePlayManager.drawAirBalloon(canvas); //Добавляем шарик
                        gamePlayManager.startObjectsGeneration(canvas, selectedLevel); //Добавляем генерацию игровых объектов
                        gamePlayManager.drawCountCoins(canvas, displayMetrics); //Рисуем количество собранных монет
                        gamePlayManager.countDistance(); //Увеличиваем пройденную дистацнию
                        gamePlayManager.drawLevelProgress(canvas);

                        if(isPaused && !levelCompleted) {
                            gamePlayManager.drawGamePlayMenu(canvas);
                        } else {
                            gamePlayManager.speedUp(); //Увеличиваем скорость игры
                        }

                        gamePlayManager.drawGearWheel(canvas); //Добавляем кнопку настроек


                        if(gamePlayManager.checkLevelProgress(levelsFinishDistance.get(selectedLevel)) && !isPaused) {
                            levelCompleted = true;
                            GamePlayManager.speed = 0;
                            switchGameStatus();
                        }

                        if(levelCompleted) {
                            gamePlayManager.drawLevelCompleted(canvas, displayMetrics, getStars()); //Считаем, что уровень пройден
                            GamePlayManager.speed = 0;

                            //Сохраняем прогресс по уровню
                            if(needSave) {
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                //Сохраняем прогресс по уровню
                                if(user.getMaxLevelStars(selectedLevel) < getStars()) {
                                    user.setLevelsProgress(selectedLevel, getStars());
                                }

                                SaveManager.save(activity, user); //Сохраняем прогресс в файл.
                                needSave = false;
                            }
                        }



                        if(gamePlayManager.getHpAirBalloon() <= 0) { //Проверяем количество здоровья
                            GamePlayManager.speed = 0;

                            gamePlayManager.switchStatusGame(true);
                            gamePlayManager.drawLevelCompleted(canvas, displayMetrics, getStars());

                            if(needSave) {
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                //Сохраняем прогресс по уровню
                                if(user.getMaxLevelStars(selectedLevel) < getStars()) {
                                    user.setLevelsProgress(selectedLevel, getStars());
                                }


                                SaveManager.save(activity, user); //Сохраняем прогресс в файл.
                                needSave = false;
                            }
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

                        if(gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.RESUME) { //Обрабатываем нажатия в меню.
                            switchGameStatus();
                        } else if((gamePlayManager.getHpAirBalloon() <= 0 || isPaused)
                                && gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.EXIT) {
                            running = false; //Останавливаем поток

                            //Создаем новую активность.
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);

                            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                            // Завершить текущую активность
                            activity.finish();
                        } else if((levelCompleted || gamePlayManager.getHpAirBalloon() <= 0) && gamePlayManager.
                                getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.RESTART) {

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
        levelCompleted=false;
        switchGameStatus();
        needSave = true;
    } //Перезапуск уровня. Работает коряво, нужно пересобирать.

    public static HashMap<Integer, Integer> loadLevelFinishInfo() {
        HashMap<Integer, Integer> levelsInfo = new HashMap<>();
        levelsInfo.put(1, 500);
        levelsInfo.put(2, 700);
        levelsInfo.put(3, 750);
        levelsInfo.put(4, 800);
        levelsInfo.put(5, 800);
        levelsInfo.put(6, 800);
        levelsInfo.put(7, 800);
        levelsInfo.put(8, 1000);
        levelsInfo.put(9, 1150);
        levelsInfo.put(10, 1500);
        levelsInfo.put(11, 1700);
        levelsInfo.put(12, 1900);
        levelsInfo.put(13, 2000);
        levelsInfo.put(14, 2300);
        levelsInfo.put(15, 2500);
        levelsInfo.put(16, 3000);

        return levelsInfo;
    }

    public static void addStars() {
        stars++;
    }

    public static int getStars() {
        return stars;
    }
}
