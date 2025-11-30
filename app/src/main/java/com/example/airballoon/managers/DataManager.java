package com.example.airballoon.managers;

import android.app.Activity;
import android.util.DisplayMetrics;

import com.example.airballoon.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//Хранит все тяжелые объекты
public class DataManager extends Thread {
    private static HashMap<Integer, Integer> levelsInfo;

    //Хранит GamePlayManager по запущенному уровню и по следующему, если такой есть
    private static List<GamePlayManager> gamePlayManagerList = Collections.synchronizedList(new ArrayList<>());

    private DataManager() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    //Загружаем все данные
    public static boolean loadData(Activity activity, User user, int levelNum) {
        loadLevelFinishInfo();
        loadGamePlayManagers(activity, user, levelNum);
        return true;
    }

    public static GamePlayManager getGamePlayManager(int levelNum) {

        for (int i = gamePlayManagerList.size() - 1; i >= 0; i--) {
            if (gamePlayManagerList.get(i).getLevelNum() == levelNum) {
                return gamePlayManagerList.get(i);
            }
        }

        return gamePlayManagerList.get(gamePlayManagerList.size() - 1);
    }

    //Презапускает текущий уровень
    public static void rebootLevel() {
        gamePlayManagerList.get(0).rebootGamePlayManager();
    }

    //Обновляет список менеджеров при переходе к следующему уровню
    public static void updateGamePlayManagerList(Activity activity, DisplayMetrics displayMetrics, User user, int levelNum) {
        //Удалили пройденный уровень
        gamePlayManagerList.remove(0);

//        //В фоновом режиме загружаем новый следующий уровень
//        LoadLevel loadLevel = new LoadLevel(activity, displayMetrics, user, levelNum);
//        loadLevel.start();


    }

//    static class LoadLevel extends Thread {
//        private final Activity activity;
//        private final DisplayMetrics displayMetrics;
//        private final User user;
//        private final int levelNum;
//
//        // Конструктор для передачи параметров потоку
//        public LoadLevel(Activity activity, DisplayMetrics displayMetrics, User user, int levelNum) {
//            this.activity = activity;
//            this.displayMetrics = displayMetrics;
//            this.user = user;
//            this.levelNum = levelNum;
//        }
//
//        @Override
//        public void run() {
//            loadNextLevel(activity, displayMetrics, user, levelNum);
//        }
//    }

    //Загружаем следующий уровень
    public static void loadNextLevel(Activity activity, DisplayMetrics displayMetrics, User user, int levelNum) {
        //Создаем менеджер следующего уровня
        if (levelNum < levelsInfo.size()) {

            GamePlayManager gamePlayManagerNext = new GamePlayManager(activity, displayMetrics, user, levelNum + 1);
            gamePlayManagerList.add(gamePlayManagerNext);
        }
    }

    //Загружает менеджер текущего уровня и следующего
    private static void loadGamePlayManagers(Activity activity, User user, int levelNum) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (!gamePlayManagerList.isEmpty()) {
            //Удаляем старые менеджеры, если такие были загружены
            gamePlayManagerList = Collections.synchronizedList(new ArrayList<>());
        }

        //Создаем менеджер текущего уровня
        GamePlayManager gamePlayManagerCurrent = new GamePlayManager(activity, displayMetrics, user, levelNum);
        gamePlayManagerList.add(gamePlayManagerCurrent);

//        //Создаем менеджер следующего уровня
//        loadNextLevel(activity, displayMetrics, user, levelNum);
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
