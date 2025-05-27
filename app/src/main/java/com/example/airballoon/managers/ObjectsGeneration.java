package com.example.airballoon.managers;

import android.app.Activity;
import android.util.DisplayMetrics;


import com.example.airballoon.game_objects.AirBalloonObject;
import com.example.airballoon.game_objects.Coin;
import com.example.airballoon.game_objects.Thorn;
import com.example.airballoon.game_objects.Wrapper;

import java.util.ArrayList;

public class ObjectsGeneration {
    //Списки объектов
    ArrayList<Wrapper> allObjects; //Список в котором лежат все доступные объекты

    ArrayList<Wrapper> usedObjects; //Список в котором лежат объекты, которые будут отрисовываться в игре
    Wrapper longThorns;

    //Игровые объекты в обертке
    Wrapper coins; //Монетка
    Wrapper thorns; //Шип
    Wrapper birds;

    public ObjectsGeneration (Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        //Создаем обертки для игровых объектов.
        coins = new Wrapper("coin", activity, displayMetrics, airBalloon);
        thorns = new Wrapper("thorn",  activity, displayMetrics, airBalloon);
        longThorns = new Wrapper("long_thorn",  activity, displayMetrics, airBalloon);
        birds = new Wrapper("bird", activity, displayMetrics, airBalloon);

        //Создаем список со всеми доступными объектами
        createAllObjects();
        createUsedObjects();
    }

    public void createUsedObjects() {
        usedObjects = new ArrayList<>();
        usedObjects.addAll(allObjects);
        updateCount();
    } //Определяет список объектов для отрисовки


    private void createAllObjects() {
        allObjects = new ArrayList<>();
        allObjects.add(coins);
        allObjects.add(thorns);
        allObjects.add(longThorns);
        allObjects.add(birds);
    } //Создает список объектов, которые будут отрисовываться в игре

    private void updateCount() {
        for(Wrapper object: usedObjects) {
            object.generateRandomCount();
        }
    } //Обновляет колличество повторений всех объектов в игровой итерации.

    public boolean checkAvailabilityCoins() {
        return coins.getDrawCount() >= 0;
    } //Проверяет, можем ли отрисовать монетки.

    public boolean checkAvailabilityThorns() {
        return thorns.getDrawCount() >= 0;
    } //Проверяет, можем ли отрисовать шипы.

    public boolean checkAvailabilityLongThorns() {
        return longThorns.getDrawCount() >= 0;
    } //Проверяет, можем ли отрисовать длинные шипы.

    public boolean checkAvailabilityBirds() {
        return birds.getDrawCount() >= 0;
    } //Проверяет, можем ли отрисовать птиц.

    public ArrayList<Wrapper> getUsedObjects() {
        return usedObjects;
    }
}
