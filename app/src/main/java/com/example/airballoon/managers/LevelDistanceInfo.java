package com.example.airballoon.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

//Класс хранит информацию по скоростям шарика на определенной дистацнии
public class LevelDistanceInfo {
    private static int magnetCount = 0;
    private static int shieldCount = 0;

    public static HashMap<Integer, Integer> getDistanceSpeeds(Integer levelNum) {
        HashMap<Integer, Integer> dSLevel = new HashMap<>();

        if(levelNum == 1) {
            dSLevel.put(0, 13);
            dSLevel.put(260_00, 14);
            dSLevel.put(340_00, 15);
            dSLevel.put(370_00, 16);
            dSLevel.put(395_00, 17);
            dSLevel.put(410_00, 18);
            dSLevel.put(420_00, 19);
            dSLevel.put(435_00, 20);
            dSLevel.put(450_00, 21);
        } else if (levelNum == 4) {
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
        } else if (levelNum == 6) {
            dSLevel.put(0, 16);
            dSLevel.put(80_00, 17);
            dSLevel.put(160_00, 18);
            dSLevel.put(260_00, 19);
            dSLevel.put(340_00, 19);
            dSLevel.put(370_00, 20);
            dSLevel.put(395_00, 21);
            dSLevel.put(410_00, 22);
            dSLevel.put(450_00, 24);
            dSLevel.put(480_00, 25);
            dSLevel.put(510_00, 27);
            dSLevel.put(515_00, 29);
            dSLevel.put(550_00, 32);
        } else if (levelNum == 8) {
            dSLevel.put(0, 20);
            dSLevel.put(80_00, 21);
            dSLevel.put(160_00, 22);
            dSLevel.put(260_00, 23);
            dSLevel.put(340_00, 24);
            dSLevel.put(370_00, 25);
            dSLevel.put(395_00, 26);
            dSLevel.put(410_00, 27);
            dSLevel.put(450_00, 28);
            dSLevel.put(480_00, 29);
            dSLevel.put(510_00, 30);
            dSLevel.put(515_00, 31);
            dSLevel.put(550_00, 32);
            dSLevel.put(570_00, 30);
            dSLevel.put(590_00, 28);
            dSLevel.put(610_00, 25);
            dSLevel.put(630_00, 24);
            dSLevel.put(650_00, 23);
            dSLevel.put(750_00, 26);
            dSLevel.put(770_00, 29);
            dSLevel.put(790_00, 31);


        } else if (levelNum == 9) {
            dSLevel.put(0, 22);
            dSLevel.put(80_00, 24);
            dSLevel.put(160_00, 26);
            dSLevel.put(260_00, 28);
            dSLevel.put(340_00, 29);
            dSLevel.put(370_00, 30);
            dSLevel.put(395_00, 31);
            dSLevel.put(410_00, 32);
        } else if (levelNum == 10) {
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
        } else if (levelNum == 11) {
            dSLevel.put(0, 22);
            dSLevel.put(80_00, 24);
            dSLevel.put(160_00, 26);
            dSLevel.put(260_00, 28);
            dSLevel.put(340_00, 29);
            dSLevel.put(370_00, 30);
            dSLevel.put(395_00, 31);
            dSLevel.put(410_00, 32);
            dSLevel.put(770_00, 31);
            dSLevel.put(790_00, 30);
            dSLevel.put(810_00, 27);
            dSLevel.put(1000_00, 26);
            dSLevel.put(1100_00, 28);
            dSLevel.put(1150_00, 30);
            dSLevel.put(1200_00, 31);
            dSLevel.put(1300_00, 32);
        }else if (levelNum == 12) {
            dSLevel.put(0, 23);
            dSLevel.put(80_00, 24);
            dSLevel.put(160_00, 27);
            dSLevel.put(260_00, 29);
            dSLevel.put(340_00, 30);
            dSLevel.put(370_00, 31);
            dSLevel.put(395_00, 32);
            dSLevel.put(1150_00, 30);
            dSLevel.put(1200_00, 29);
            dSLevel.put(1300_00, 28);
            dSLevel.put(1350_00, 27);
            dSLevel.put(1400_00, 26);
        } else if (levelNum == 13) {
            dSLevel.put(0, 20);
            dSLevel.put(80_00, 21);
            dSLevel.put(160_00, 22);
            dSLevel.put(260_00, 23);
            dSLevel.put(340_00, 24);
            dSLevel.put(370_00, 25);
            dSLevel.put(395_00, 26);
            dSLevel.put(410_00, 27);
            dSLevel.put(450_00, 28);
            dSLevel.put(480_00, 29);
            dSLevel.put(510_00, 30);
            dSLevel.put(515_00, 31);
            dSLevel.put(550_00, 32);
            dSLevel.put(570_00, 30);
            dSLevel.put(590_00, 28);
            dSLevel.put(610_00, 25);
            dSLevel.put(630_00, 24);
            dSLevel.put(650_00, 23);
            dSLevel.put(750_00, 26);
            dSLevel.put(770_00, 29);
            dSLevel.put(790_00, 31);
        } else if (levelNum == 14) {
            dSLevel.put(0, 23);
            dSLevel.put(80_00, 24);
            dSLevel.put(160_00, 27);
            dSLevel.put(260_00, 29);
            dSLevel.put(340_00, 30);
            dSLevel.put(370_00, 31);
            dSLevel.put(395_00, 32);
            dSLevel.put(1150_00, 30);
            dSLevel.put(1200_00, 29);
            dSLevel.put(1300_00, 28);
            dSLevel.put(1350_00, 27);
            dSLevel.put(1400_00, 26);
            dSLevel.put(1500_00, 28);
            dSLevel.put(1600_00, 30);
            dSLevel.put(1700_00, 32);
        } else if (levelNum == 15 || levelNum == 16) {
            dSLevel.put(0, 20);
            dSLevel.put(80_00, 21);
            dSLevel.put(160_00, 22);
            dSLevel.put(260_00, 23);
            dSLevel.put(340_00, 25);
            dSLevel.put(370_00, 26);
            dSLevel.put(395_00, 27);
            dSLevel.put(410_00, 28);
            dSLevel.put(450_00, 29);
            dSLevel.put(480_00, 30);
            dSLevel.put(510_00, 31);
            dSLevel.put(515_00, 32);
            dSLevel.put(810_00, 30);
            dSLevel.put(1000_00, 31);
            dSLevel.put(1100_00, 32);
        }else {
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

    public static int getMaxBirdCount(int levelNum) {
        switch (levelNum) {
            case 4:
                return 1;
            case 5:
                return 10;
            case 6:
                return 3;
            case 7:
                return 2;
            case 9:
                return 1;
            case 10:
                return 1;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            case 14:
                return 3;
            case 15:
                return 3;
            case 16:
                return 3;
        }

        return 0;
    }

    public static HashMap<String, Integer> getDistanceAddition(Integer levelNum) {
        HashMap<String, Integer> dALevel = new HashMap<>();


        if(levelNum == 1 || levelNum == 2) {
            dALevel.put("minDistanceAdditionObject", 350);
            dALevel.put("maxDistanceAdditionObject", 400);
        } else if (levelNum == 3) {
            dALevel.put("minDistanceAdditionObject", 350);
            dALevel.put("maxDistanceAdditionObject", 400);
        } else {
            dALevel.put("minDistanceAdditionObject", 350);
            dALevel.put("maxDistanceAdditionObject", 400);
        }

        return dALevel;
    }

    public static int getBirdStartDistance(Integer levelNum) {
        if(levelNum == 5) {
            return 50_00;
        } else if(levelNum == 9) {
            return 50_00;
        } else if(levelNum == 10) {
            return 50_00;
        } else if(levelNum == 11) {
            return 1000_00;
        } else if(levelNum == 12) {
            return 100_00;
        } else if(levelNum == 13) {
            return 300_00;
        } else if(levelNum == 14) {
            return 800_00;
        } else if(levelNum == 15 || levelNum == 16) {
            return 500_00;
        }else {
            return 0;
        }
    }

    public static int getMagnetStartDistance(Integer levelNum) {
        if(levelNum == 3 || levelNum == 4|| levelNum == 5) {
            return 250_00;
        } else if (levelNum == 7) {
            return 550_00;
        } else if (levelNum == 9) {
            return 50_00;
        } else if (levelNum == 11) {
            return 500_00;
        } else if (levelNum == 12) {
            return 100_00;
        } else if (levelNum == 15 || levelNum == 16) {
            return 600_00;
        }else {
            return 0;
        }
    }

    public static int getShieldStartDistance(Integer levelNum) {
        if(levelNum == 6) {
            return 450_00;
        } else if (levelNum == 9) {
            return 700_00;
        } else if (levelNum == 10) {
            return 1000_00;
        } else if (levelNum == 11) {
            return 410_00;
        } else if (levelNum == 12) {
            return 500_00;
        } else if (levelNum == 13) {
            return 1000_00;
        }else if (levelNum == 14) {
            return 1000_00;
        } else if (levelNum == 15 || levelNum == 16) {
            return 1500_00;
        }else {
            return 0;
        }
    }

    public static boolean hadMagnet(Integer levelNum) {
        if(levelNum == 2 && magnetCount < 1) {
            return true;
        } if(levelNum == 4 && magnetCount < 1) {
            return true;
        } if(levelNum == 7 && magnetCount < 1) {
            return true;
        } if(levelNum == 9 && magnetCount < 1) {
            return true;
        } if(levelNum == 10 && magnetCount < 2) {
            return true;
        } if(levelNum == 11 && magnetCount < 1) {
            return true;
        } if(levelNum == 12 && magnetCount < 2) {
            return true;
        } if(levelNum == 13 && magnetCount < 2) {
            return true;
        } if(levelNum == 14 && magnetCount < 2) {
            return true;
        } if((levelNum == 15 || levelNum == 16) && magnetCount < 2) {
            return true;
        }else {
            return false;
        }
    } //Сколько раз можем отрисовать магнит в зависимости от уровня

    public static boolean hadShield(Integer levelNum) {
        if(levelNum == 6 && shieldCount < 1) {
            return true;
        } else if (levelNum == 9 && shieldCount < 1) {
            return true;
        } else if (levelNum == 10 && shieldCount < 1) {
            return true;
        } else if (levelNum == 11 && shieldCount < 1) {
            return true;
        } else if (levelNum == 11 && shieldCount < 2) {
            return true;
        } else if (levelNum == 12 && shieldCount < 2) {
            return true;
        } else if (levelNum == 13 && shieldCount < 2) {
            return true;
        } else if (levelNum == 14 && shieldCount < 1) {
            return true;
        } if(levelNum == 15 || levelNum == 16 && shieldCount < 3) {
            return true;
        }else {
            return false;
        }
    } //Сколько раз можем отрисовать магнит в зависимости от уровня

    public static LinkedHashMap<Integer, Boolean> getLongThornDistance(int levelNum) {
        LinkedHashMap<Integer, Boolean> longThornDistance = new LinkedHashMap<>();

        if(levelNum == 7 || levelNum == 3) {
            longThornDistance.put(100_00, false);
            longThornDistance.put(150_00, false);
            longThornDistance.put(300_00, false);
        }

        if(levelNum == 8) {
            longThornDistance.put(10_00, false);
            longThornDistance.put(30_00, false);
            longThornDistance.put(50_00, false);
            longThornDistance.put(60_00, false);
            longThornDistance.put(100_00, false);
            longThornDistance.put(140_00, false);
            longThornDistance.put(200_00, false);
            longThornDistance.put(220_00, false);
        }

        if(levelNum == 9) {
            longThornDistance.put(10, false);
            longThornDistance.put(20, false);
            longThornDistance.put(30, false);
            longThornDistance.put(40, false);
            longThornDistance.put(50, false);
            longThornDistance.put(60, false);
            longThornDistance.put(70, false);
        }

        if(levelNum == 10) {
            longThornDistance.put(500_00, false);
            longThornDistance.put(800_00, false);
        }

        if(levelNum == 11) {
            longThornDistance.put(50_00, false);
        }

        if(levelNum == 12) {
            longThornDistance.put(50_00, false);
            longThornDistance.put(150_00, false);
            longThornDistance.put(550_00, false);
        }

        if(levelNum == 13) {
            longThornDistance.put(50_00, false);
            longThornDistance.put(150_00, false);
            longThornDistance.put(550_00, false);
            longThornDistance.put(650_00, false);
        }

        if(levelNum == 14) {
            longThornDistance.put(50_00, false);
            longThornDistance.put(150_00, false);
            longThornDistance.put(250_00, false);
            longThornDistance.put(350_00, false);
            longThornDistance.put(450_00, false);
        }

        if(levelNum == 15 || levelNum == 16) {
            longThornDistance.put(50_00, false);
            longThornDistance.put(150_00, false);
            longThornDistance.put(250_00, false);
            longThornDistance.put(350_00, false);
            longThornDistance.put(450_00, false);
            longThornDistance.put(1350_00, false);
            longThornDistance.put(1450_00, false);
        }

        return longThornDistance;
    } //Дистацнии на которых котороткие шипы начнут меняться на длинные

    public static void addMagnetCounter() {
        magnetCount++;
    }

    public static void addShieldCounter() {
        shieldCount++;
    }

    public static void resetCounter() {
        magnetCount = 0;
    }
}
