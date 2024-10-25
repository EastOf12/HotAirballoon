package com.example.airballoon.models;

import android.app.Activity;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.example.airballoon.R;

import java.util.HashMap;
import java.util.Objects;

public class User {

    private long coins = 0;

    private int lastCoins = 0; //Монетки, которые были начислены в крайней игре
    private long maxDistanceLevelFirst = 0;

    private int selectAirBalloon;

    public User() {
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
