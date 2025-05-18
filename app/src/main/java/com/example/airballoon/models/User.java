package com.example.airballoon.models;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class User {

    private long coins = 0;

    private int lastCoins = 0; //Монетки, которые были начислены в крайней игре
    private long maxDistanceLevelFirst = 0;

    private int selectAirBalloon;
    private List<Integer> availableBalls;

    public User() {
        availableBalls = new ArrayList<>();
        availableBalls.add(1);
        selectAirBalloon = 1;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "coins=" + coins +
                ", maxDistanceLevelFirst=" + maxDistanceLevelFirst +
                ", lastCoins=" + lastCoins +
                ", selectAirBalloon=" + selectAirBalloon +
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
}
