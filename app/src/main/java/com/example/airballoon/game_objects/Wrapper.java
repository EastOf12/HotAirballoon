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
    private final String type; //Название объектов
    Random random;
    private int maxCount; //Максимальное количество объектов
    private int drawCount; //Общее количество доступных объектов на отрисовку в рамках игровой итерации
    private ArrayList<Object> objects; //Игровые объекты, зависит от переданного типа
    private boolean newIteration;

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
                maxCount = 5; //8
                break;
            case "thorn":
                maxCount = 10; //4
                break;
            case "long_thorn":
                maxCount = 0; //0
                break;
            case "bird":
                maxCount = 0; //0
                break;
            case "shield":
                maxCount = 1; //0
                break;
            case "magnet":
                maxCount = 1;
                break;
            case "star":
                maxCount = 0;
                break;
        }
    } //Максимальное количество возможных объектов в пуле

    private void generateGameObjects() {
        objects = new ArrayList<>();

        switch (type) {
            case "coin":
                //Создаем необходимое количество объектов монет
                for (int i = 0; i <= maxCount - 1; i++) {
                    objects.add(new Coin(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "thorn":
                //Создаем необходимое количество объектов шипов
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new Thorn(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "long_thorn":
                //Создаем необходимое количество длинных шипов
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new LongThorn(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "bird":
                //Создаем необходимое количество птиц
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new Bird(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "shield":
                //Создаем необходимое количество щитов
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new Shield(activity, displayMetrics, airBalloonObject));
                }
                break;
            case "magnet":
                //Создаем необходимое количество магнитов
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new Magnet(activity, displayMetrics, airBalloonObject));
                }
                break;

            case "star":
                //Создаем необходимое количество звезд
                for (int i = 0; i <= maxCount; i++) {
                    objects.add(new Star(activity, displayMetrics, airBalloonObject));
                }
                break;
        }
    } //Генерирует игровые объекты

    public int getDrawCount() {
        return drawCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        generateGameObjects();

    }

    public boolean drawObjects(Canvas canvas, Integer count, String wrapperType) {

        newIteration = true;

        //Не даем отрисовать больше чем можем.
        if (count > drawCount) {
            count = drawCount;
        }

        if (wrapperType.equals("coin")) {
            for (int i = 0; i <= count; i++) {
                Coin coin = (Coin) objects.get(i);
                coin.draw(canvas);

                if (coin.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("thorn")) {
            for (int i = 0; i <= count; i++) {
                Thorn thorn = (Thorn) objects.get(i);
                thorn.drawThorn(canvas);

                if (thorn.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("long_thorn")) {
            for (int i = 0; i <= count; i++) {
                LongThorn longThorn = (LongThorn) objects.get(i);
                longThorn.drawThorn(canvas);

                if (longThorn.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("bird")) {
            for (int i = 0; i <= count; i++) {
                Bird bird = (Bird) objects.get(i);
                bird.draw(canvas);

                if (bird.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("shield")) {
            for (int i = 0; i <= count; i++) {
                Shield shield = (Shield) objects.get(i);
                shield.draw(canvas);

                if (shield.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("magnet")) {
            for (int i = 0; i <= count; i++) {
                Magnet magnet = (Magnet) objects.get(i);
                magnet.draw(canvas);

                if (magnet.isNeedDraw()) {
                    newIteration = false;
                }
            }
        } else if (wrapperType.equals("star")) {
            for (int i = 0; i <= count; i++) {
                Star star = (Star) objects.get(i);
                star.draw(canvas);

                if (star.isNeedDraw()) {
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
            case "bird":
                objects.add(new Bird(activity, displayMetrics, airBalloonObject));
                drawCount++;
                break;
            case "shield":
                objects.add(new Shield(activity, displayMetrics, airBalloonObject));
                drawCount++;
                break;
            case "magnet":
                objects.add(new Magnet(activity, displayMetrics, airBalloonObject));
                drawCount++;
                break;
            case "star":
                objects.add(new Star(activity, displayMetrics, airBalloonObject));
                drawCount++;
                break;
        }
    } //Добавляем объект в пул

    public void addObject(Bird bird) {
        objects.add(bird);
        drawCount++;
    } //Для птички отдельно, тк ее нужно создавать на экране загрузки либо в concurrent
}
