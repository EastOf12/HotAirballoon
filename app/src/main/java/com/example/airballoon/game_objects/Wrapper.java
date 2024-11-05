package com.example.airballoon.game_objects;

import java.util.Random;

//Обертка для всех объектов, которые будут генироваться в игре
public class Wrapper {

    private int maxCount; //Максимальное количество повторений
    private int minCount; //Минимальное количество повторений
    private int drawCount; //Сколько раз должен отрисовываться объект в рамках одной игровой итерации.
    private final String type; //Название объекта
    private final Object object; //Сам игровой объект
    Random random;

    public Wrapper(String type, Object object) {
        this.type = type;
        this.object = object;
        random = new Random();
        generateMaxMin();
    }

    public void generateRandomCount() {
        drawCount = random.nextInt(maxCount - minCount + 1) + minCount;
    } //Генерирует случайное количество повторений объекта в игровой итерации

    private void generateMaxMin() {
        switch (type) {
            case "coin":
                maxCount = 20;
                minCount = 10;
                break;
            case "thorn":
                maxCount = 15;
                minCount = 5;
                break;
        }
    } //Генерирует максимальное и минимальное количество объектов в зависимости от их типа.

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public String getType() {
        return type;
    }

    public Object getObject() {
        return object;
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
}
