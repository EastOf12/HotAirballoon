package com.example.airballoon.managers;

import java.util.ArrayList;
import java.util.HashMap;

//Класс хранит информацию по скоростям шарика на определенной дистацнии
public class LevelDistanceInfo {
    private static int magnetCount = 0;

    public static HashMap<Integer, Integer> getDistanceSpeeds(Integer levelNum) {
        HashMap<Integer, Integer> dSLevel = new HashMap<>();

        if(levelNum == 1) {
            dSLevel.put(0, 15);
            dSLevel.put(80_00, 16);
            dSLevel.put(160_00, 17);
            dSLevel.put(260_00, 18);
            dSLevel.put(340_00, 19);
            dSLevel.put(370_00, 20);
            dSLevel.put(395_00, 21);
            dSLevel.put(410_00, 22);
            dSLevel.put(420_00, 23);
            dSLevel.put(435_00, 24);
            dSLevel.put(450_00, 25);
        } else if (levelNum == 2) {
            dSLevel.put(0, 16);
            dSLevel.put(80_00, 17);
            dSLevel.put(160_00, 18);
            dSLevel.put(260_00, 19);
            dSLevel.put(340_00, 19);
            dSLevel.put(370_00, 20);
            dSLevel.put(395_00, 21);
            dSLevel.put(410_00, 22);
            dSLevel.put(450_00, 23);
            dSLevel.put(480_00, 24);
            dSLevel.put(510_00, 25);
            dSLevel.put(515_00, 26);
            dSLevel.put(550_00, 27);
        }

        return dSLevel;
    }

    public static HashMap<String, Integer> getDistanceAddition(Integer levelNum) {
        HashMap<String, Integer> dALevel = new HashMap<>();

        if(levelNum == 1 || levelNum == 2) {
            dALevel.put("minDistanceAdditionObject", 250);
            dALevel.put("maxDistanceAdditionObject", 400);
        }


        return dALevel;
    }

    public static int getBirdStartDistance(Integer levelNum) {
        if(levelNum == 2) {
            return 450_00;
        } else {
            return 0;
        }
    }

    public static int getMagnetStartDistance(Integer levelNum) {
        if(levelNum == 1 || levelNum == 2) {
            return 250_00;
        } else {
            return 0;
        }
    }

    public static boolean hadMagnet(Integer levelNum) {
        if(levelNum == 1 && magnetCount >= 1) {
            return false;
        } else if (levelNum == 2 && magnetCount >= 1) {
            return false;
        } else {
            return true;
        }
    }

    public static void addMagnetCounter() {
        magnetCount++;
    }

    public static void resetCounter() {
        magnetCount = 0;
    }
}
