package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

//Обертка для всех объектов, которые будут генироваться в игре
public class Wrapper {
    private final Activity activity;
    private final DisplayMetrics displayMetrics;
    private final AirBalloonObject airBalloonObject;

    private int maxCount; //Максимальное количество объектов
    private int minCount; //Минимальное количество объектов
    private int drawCount; //Общее количество доступных объектов на отрисовку в рамках игровой итерации
    private final String type; //Название объектов

    private ArrayList<Object> objects; //Игровые объекты, например монетки
    private boolean newIteration;

    Random random;

    public Wrapper(String type, Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloonObject) {
        this.type = type;
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.airBalloonObject = airBalloonObject;
        random = new Random();
        generateMaxMin();
        generateGameObjects();
        newIteration = true;
    }

    public void generateRandomCount() {
        drawCount = random.nextInt(maxCount - minCount + 1) + minCount;
    } //Генерирует случайное количество повторений объекта в игровой итерации

    private void generateMaxMin() {
        switch (type) {
            case "coin":
                maxCount = 5;
                minCount = 5;
                break;
            case "thorn":
                maxCount = 15;
                minCount = 5;
                break;
        }
    } //Генерирует максимальное и минимальное количество объектов в зависимости от их типа.

    private void generateGameObjects() {
        objects = new ArrayList<>();

        switch (type) {
            case "coin":

                //Создаем необходимое количество объектов монет
                for(int i = 0; i <= maxCount; i++) {
                    objects.add(new Coin(activity, displayMetrics, airBalloonObject));
                }

                break;
            case "thorn":
                //Создаем необходимое количество объектов шипов
                for(int i = 0; i <= maxCount; i++) {
                    objects.add(new Thorn(activity, displayMetrics, airBalloonObject));
                }
                break;
        }
    } //Генерирует игровые объекты

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public String getType() {
        return type;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }
    public boolean drawObjects(Canvas canvas, Integer count, String wrapperType) {

        newIteration = true;



        //Не даем отрисовать больше чем можем.
        if(count > drawCount) {
            count = drawCount;
        }

        if(wrapperType.equals("coin")) {
            for(int i = 0; i <= count; i++ ) {
                Coin coin = (Coin) objects.get(i);
                coin.draw(canvas);

                if(coin.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("thorn")) {
            for(int i = 0; i <= count; i++ ) {
                Thorn thorn = (Thorn) objects.get(i);
                thorn.drawThorn(canvas);

                if(thorn.isNeedDraw()) {
                    newIteration = false;
                }
            }
        }


        return newIteration;
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    @NonNull
    @Override
    public String toString() {
        return "Wrapper{" +
                "maxCount=" + maxCount +
                ", minCount=" + minCount +
                ", drawCount=" + drawCount +
                ", type='" + type + '\'' +
                ", random=" + random +
                '}';
    }
}
