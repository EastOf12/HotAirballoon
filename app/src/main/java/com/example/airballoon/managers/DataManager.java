package com.example.airballoon.managers;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.example.airballoon.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Хранит все тяжелые объекты
public class DataManager extends Thread{
    private static HashMap<Integer, Integer> levelsInfo;
    private static List<GamePlayManager> gamePlayManagerList = new ArrayList<>();

    private DataManager() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    //Загружаем все данные
    public static boolean loadData(Activity activity, User user, int levelNum, int distance, int coins) {
        loadLevelFinishInfo();
        loadGamePlayManager(activity, user, levelNum, distance, coins);
        return true;
    }

    public static GamePlayManager getGamePlayManager(int levelNum) {
        for (int i=gamePlayManagerList.size() - 1; i>=0; i--) {
            if(gamePlayManagerList.get(i).getLevelNum() == levelNum) {
                return gamePlayManagerList.get(i);
            }
        }

        return gamePlayManagerList.get(gamePlayManagerList.size() - 1);
    }

    private static void loadGamePlayManager(Activity activity, User user, int levelNum, int distance, int coins) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gamePlayManagerList.add(new GamePlayManager(activity, displayMetrics, user, levelNum, distance, coins));

        if(gamePlayManagerList.size() >= 4) {
            gamePlayManagerList.remove(0);
        }
    }

    public static HashMap<Integer, Integer> getLevelFinishInfo() {
        return levelsInfo;
    }


    //Загружаем информацию по длине уровней
    private static void loadLevelFinishInfo() {
        levelsInfo = new HashMap<>();
        levelsInfo.put(1, 500);
        levelsInfo.put(2, 700);
        levelsInfo.put(3, 750);
        levelsInfo.put(4, 800);
        levelsInfo.put(5, 800);
        levelsInfo.put(6, 800);
        levelsInfo.put(7, 800);
        levelsInfo.put(8, 1000);
        levelsInfo.put(9, 1000);
        levelsInfo.put(10, 1000);
        levelsInfo.put(11, 1100);
        levelsInfo.put(12, 1200);
        levelsInfo.put(13, 1300);
        levelsInfo.put(14, 1300);
        levelsInfo.put(15, 1400);
        levelsInfo.put(16, 1500);
    }
}
