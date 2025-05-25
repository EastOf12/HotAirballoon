package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

//Обертка для всех объектов, которые будут генироваться в игре
public class Wrapper {
    private final Activity activity;
    private final DisplayMetrics displayMetrics;
    private final AirBalloonObject airBalloonObject;

    private int maxCount; //Максимальное количество объектов
    private int drawCount; //Общее количество доступных объектов на отрисовку в рамках игровой итерации
    private final String type; //Название объектов

    private ArrayList<Object> objects; //Игровые объекты, зависит от переданного типа
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
        drawCount = maxCount - 1;
    } //Генерирует количество повторений объекта в игровой итерации. Когда 0, будет 1 объект, а если -1, то объекта не будет.

    private void generateMaxMin() {
        switch (type) {
            case "coin":
                maxCount = 8; //8
                break;
            case "thorn":
                maxCount = 4; //4
                break;
            case "long_thorn":
                maxCount = 0;
                break;
        }
    } //Максимальное количество возможных объектов в пуле

    private void generateGameObjects() {
        objects = new ArrayList<>();

        switch (type) {
            case "coin":
                //Создаем необходимое количество объектов монет
                for(int i = 0; i <= maxCount - 1; i++) {
                    objects.add(new Coin(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "thorn":
                //Создаем необходимое количество объектов шипов
                for(int i = 0; i <= maxCount; i++) {
                    objects.add(new Thorn(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "long_thorn":
                //Создаем необходимое количество длинных шипов
                for(int i = 0; i <= maxCount; i++) {
                    objects.add(new LongThorn(activity, displayMetrics, airBalloonObject));
                }
                break;
        }
    } //Генерирует игровые объекты

    public int getDrawCount() {
        return drawCount;
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
        } else if (wrapperType.equals("long_thorn")) {
            for(int i = 0; i <= count; i++ ) {
                LongThorn longThorn = (LongThorn) objects.get(i);
                longThorn.drawThorn(canvas);

                if(longThorn.isNeedDraw()) {
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
                ", drawCount=" + drawCount +
                ", type='" + type + '\'' +
                ", random=" + random +
                ", objects=" + objects +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wrapper wrapper = (Wrapper) o;
        return maxCount == wrapper.maxCount && drawCount == wrapper.drawCount && newIteration == wrapper.newIteration && Objects.equals(activity, wrapper.activity) && Objects.equals(displayMetrics, wrapper.displayMetrics) && Objects.equals(airBalloonObject, wrapper.airBalloonObject) && Objects.equals(type, wrapper.type) && Objects.equals(objects, wrapper.objects) && Objects.equals(random, wrapper.random);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, displayMetrics, airBalloonObject, maxCount, drawCount, type, objects, newIteration, random);
    }

    public void removeObject() {
        objects.remove(objects.size() - 1);
        drawCount--;
    } //Удаляем первый объект в пуле

    public void addObject() {
        switch (type) {
            case "long_thorn":
                objects.add(new LongThorn(activity, displayMetrics, airBalloonObject));
                drawCount++;
                break;
        }
    } //Добавляем объект в пул
}
