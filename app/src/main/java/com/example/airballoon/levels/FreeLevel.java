package com.example.airballoon.levels;

import static com.example.airballoon.managers.DataManager.getLevelFinishInfo;
import static com.example.airballoon.managers.DataManager.loadData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import com.example.airballoon.GamePlayActivity;
import com.example.airballoon.LoadLevelActivity;
import com.example.airballoon.MainActivity;
import com.example.airballoon.R;
import com.example.airballoon.RewardedAdActivity;
import com.example.airballoon.managers.DataManager;
import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.managers.SaveManager;

import java.time.LocalDateTime;
import java.util.HashMap;

@SuppressLint("ViewConstructor")
public class FreeLevel extends BaseLevel implements Runnable{
    private final int selectedLevel;
    private boolean levelCompleted = false;
    private static int stars = 0;
    private final HashMap<Integer, Integer> levelsFinishDistance = getLevelFinishInfo();

    public FreeLevel(Activity activity, int selectedLevel, int distance, int coins) {
        super(activity);
        gamePlayManager = DataManager.getGamePlayManager(selectedLevel);
        gamePlayManager.setDistance(distance);
        gamePlayManager.setCoins(coins);
        this.selectedLevel = selectedLevel;
    }

    //Основной цикл игры.
    @Override
    public void run() {
        stars = 0;
        gamePlayManager.startBgSound();

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


                        if(selectedLevel > 0 && gamePlayManager.checkLevelProgress(levelsFinishDistance.get(selectedLevel)) && !isPaused) {
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
                                if(selectedLevel > 0 && user.getMaxLevelStars(selectedLevel) < getStars()) {
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
                                && gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.NEXT) {
                            int nextLevel = selectedLevel;

                            if(selectedLevel <= DataManager.getLevelFinishInfo().size()) {
                                nextLevel++;
                            }

                            running = false; //Останавливаем поток

                            if(needSave) {
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                //Сохраняем прогресс по уровню
                                if(selectedLevel > 0 && user.getMaxLevelStars(selectedLevel) < getStars()) {
                                    user.setLevelsProgress(selectedLevel, getStars());
                                }

                                SaveManager.save(activity, user); //Сохраняем прогресс в файл.
                                needSave = false;
                            }

//                            DataManager.updateGamePlayManagerList(activity, displayMetrics, user, selectedLevel);

                            Intent intent = new Intent(activity, LoadLevelActivity.class);
                            intent.putExtra("levelNumber", nextLevel);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(0, 0);
                            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            // Завершить текущую активность
                            activity.finish();

                        } else if((gamePlayManager.getHpAirBalloon() <= 0 || isPaused)
                                && gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.EXIT) {
                            running = false; //Останавливаем поток

                            //Создаем новую активность.
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);

                            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            gamePlayManager.bgStopSound();
                            // Завершить текущую активность
                            activity.finish();
                        } else if((levelCompleted || gamePlayManager.getHpAirBalloon() <= 0) && gamePlayManager.
                                getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.RESTART) {
                            running = false; //Останавливаем поток

                            if(needSave) {
                                user.addCoins(gamePlayManager.getCollectedCoins());
                                user.addMaxDistanceLevelFirst(gamePlayManager.getDistance());
                                //Сохраняем прогресс по уровню
                                if(selectedLevel > 0 && user.getMaxLevelStars(selectedLevel) < getStars()) {
                                    user.setLevelsProgress(selectedLevel, getStars());
                                }

                                SaveManager.save(activity, user); //Сохраняем прогресс в файл.
                                needSave = false;
                            }

                            Intent intent = new Intent(activity, LoadLevelActivity.class);
                            intent.putExtra("levelNumber", selectedLevel);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(0, 0);
                            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            // Завершить текущую активность
                            activity.finish();

                        }

                        else if((levelCompleted || gamePlayManager.getHpAirBalloon() <= 0) &&
                                gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.MARKETING_MONEY) {

                            running = false;
                            Intent intent = new Intent(activity, RewardedAdActivity.class); //Создаем активность с рекламой
                            intent.putExtra("typeReward", 1);
                            activity.startActivity(intent);
                            activity.finish(); // Завершаем текущую активность
                        } //Удваиваем деньги

                        else if((levelCompleted || gamePlayManager.getHpAirBalloon() <= 0) &&
                                gamePlayManager.getGamePlayMenu().onTouch(event, isPaused, levelCompleted) == MenuActions.MARKETING_ADD_HP) {
                            running = false;
                            Intent intent = new Intent(activity, RewardedAdActivity.class); //Создаем активность с рекламой
                            intent.putExtra("levelNumber", selectedLevel);
                            intent.putExtra("distance", gamePlayManager.getDistance());
                            intent.putExtra("coins", gamePlayManager.getCollectedCoins());
                            activity.startActivity(intent);
                            activity.finish(); // Завершаем текущую активность
                        } //Даем еще одну попытку


                        if(!isPaused && gamePlayManager.getHpAirBalloon()> 0) {
                            return gamePlayManager.onTouchAirBalloon(event);
                        } else {
                            return true;
                        }
                    }
                });
            }
        }
    }

    public static void addStars() {
        stars++;
    }

    public static int getStars() {
        return stars;
    }

    public static void rebootStars() {
        stars = 0;
    }
}
