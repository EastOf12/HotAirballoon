package com.example.airballoon.managers;

public class ManagerFPS {
    final float FPS;
    final float SECOND;
    final float UPDATE_TIME;
    int counterFPS;
    int previousSecond;
    int currentSecond;
    float lastTime;
    float delta;
    float nowTime;
    float elapsedTime;

    public ManagerFPS() {
        counterFPS = 0;
        previousSecond = -1;
        FPS = 60;
        SECOND = 1000000000;
        UPDATE_TIME = SECOND / FPS;
        lastTime = System.nanoTime();
        delta = 0;
    }

    public void printFPS() {
        currentSecond = (int) (System.currentTimeMillis() / 1000);

        if (currentSecond != previousSecond) {
            previousSecond = currentSecond;
            counterFPS = 0;
        } else {
            counterFPS++;
        }
    }

    public boolean lockFPS() {
        nowTime = System.nanoTime();
        elapsedTime = nowTime - lastTime;
        lastTime = nowTime;
        delta += elapsedTime / UPDATE_TIME;

        if (delta > 1) {
            delta--;
            return true;
        } else {
            return false;
        }
    }
}
