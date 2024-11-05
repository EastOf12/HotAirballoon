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

    //Игровые объекты в обертке
    Wrapper coin; //Монетка
    Wrapper thorn; //Шип

    public ObjectsGeneration (Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        //Создаем обертки для игровых объектов.
        coin = new Wrapper("coin", new Coin(activity, displayMetrics, airBalloon));
        thorn = new Wrapper("thorn", new Thorn(activity, displayMetrics, airBalloon));

        //Создаем список со всеми доступными объектами
        createAllObjects();
    }

    public void createUsedObjects() {
        usedObjects.addAll(allObjects);
        updateCount();

        System.out.println("Список объектов, который получили " + usedObjects);
    } //Определяет список объектов для отрисовки


    private void createAllObjects() {
        allObjects = new ArrayList<>();
        allObjects.add(coin);
        allObjects.add(thorn);
    } //Создает список объектов, которые будут отрисовываться в игре

    private void updateCount() {
        for(Wrapper object: usedObjects) {
            object.generateRandomCount();
        }
    } //Обновляет колличество повторений всех объектов в игровой итерации.
}
