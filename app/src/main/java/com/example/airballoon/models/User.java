package com.example.airballoon.models;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {

    private long coins = 0;

    private int lastCoins = 0; //Монетки, которые были начислены в крайней игре
    private long maxDistanceLevelFirst = 0;

    private int selectAirBalloon;
    private List<Integer> availableBalls;

    private final HashMap<Integer, Integer> levelsProgress = new HashMap<>();
    private final int maxLevelsCount = 16; //Количество уровней в игре


    public User() {
        availableBalls = new ArrayList<>();
        availableBalls.add(1);
        selectAirBalloon = 1;

        //Запоминаем, что нет пройденных уровней
        for (int i=1; i <= maxLevelsCount; i++) {
            levelsProgress.put(i, 0);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "coins=" + coins +
                ", maxDistanceLevelFirst=" + maxDistanceLevelFirst +
                ", lastCoins=" + lastCoins +
                ", selectAirBalloon=" + selectAirBalloon +
                ", availableBalls=" + availableBalls +
                ", levelsProgress=" + levelsProgress +
                '}';
    }



    public void addCoins(long coins) {
        this.coins += coins;
        lastCoins = (int) coins;
    }

    public boolean takeCoins(int coins) {
        if(this.coins >= coins) {
            this.coins -= coins;
            return true;
        }

        return false;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addAirBalloon(Integer idAirballoon) {
        availableBalls.add(idAirballoon);
    } //Добавляем шарик в доступные пользователю

    public List<Integer> getAvailableBalls() {
        return availableBalls;
    } //Возвращает шарики, которые есть у пользователя

    public void addMaxDistanceLevelFirst(int distance) {
        //Переводим дистацию в адекватный формат.
        distance = distance / 100;

        if (maxDistanceLevelFirst < distance) {
            maxDistanceLevelFirst = distance;
        }
    }

    public long getCoins() {
        return coins;
    }

    public int getLastCoins() {
        return lastCoins;
    }

    public long getMaxDistanceLevelFirst() {
        return maxDistanceLevelFirst;
    }
    public int getSelectAirBalloon() {
        return selectAirBalloon;
    }

    public void setSelectAirBalloon(int selectAirBalloon) {
        this.selectAirBalloon = selectAirBalloon;
    }

    public void setLevelsProgress(int levelNum, int starsCount) {
        levelsProgress.put(levelNum, starsCount);
    }

    public int getMaxLevelStars(int levelNum) {
        return levelsProgress.get(levelNum);
    }

    public int getStars() {
        int sum = 0;
        for (Integer value : levelsProgress.values()) {
            sum += value;
        }

        return sum;
    }
}
