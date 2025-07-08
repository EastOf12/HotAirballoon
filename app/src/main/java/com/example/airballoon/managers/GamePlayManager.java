package com.example.airballoon.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.game_objects.AirBalloonObject;
import com.example.airballoon.game_objects.BackGround;
import com.example.airballoon.game_objects.Bird;
import com.example.airballoon.game_objects.Coin;
import com.example.airballoon.game_objects.GamePlayMenu;
import com.example.airballoon.game_objects.GearWheel;
import com.example.airballoon.game_objects.LongThorn;
import com.example.airballoon.game_objects.Magnet;
import com.example.airballoon.game_objects.PosX;
import com.example.airballoon.game_objects.Shield;
import com.example.airballoon.game_objects.Thorn;
import com.example.airballoon.R;
import com.example.airballoon.game_objects.Wrapper;
import com.example.airballoon.models.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class GamePlayManager {
    Activity activity;
    DisplayMetrics displayMetrics;
    Paint textPaint;
    Paint textPaintEndGame;
    Paint textPaintDistance;
    public static int speed = 19; //Стартовая скорость
    public final int initialSpeed = speed; //Скорость при перезапуске
    GamePlayMenu gamePlayMenu;
    Random random = new Random();
    private final int speedUpInterval = 15;
    int distance = 0;
    LocalTime currentTime;

    AirBalloonObject airBalloon;
    BackGround backGround;
    GearWheel gearWheel;
    MediaPlayer mediaPlayer;
    static GameStatus gameStatus;
    private User user;
    private Bitmap image;

    //Все что относится к генерации
    private ObjectsGeneration objectsGeneration;
    private ArrayList<Wrapper> usedObjects;
    boolean needZeroCoins = true;
    boolean needZeroThorn;
    boolean needZeroLongThorn;
    boolean needZeroBird;
    boolean needZeroShield;
    boolean needZeroMagnet;
    private int minDistanceAdditionObject = 250; //Минимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int maxDistanceAdditionObject = 450; //Максимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int distanceAdditionObject = 35; //Дистацния при достижении которой добавляем новый объект в пул
    private int pullCoinsCount = 0; //Число монеток подряд добавленных в пул.
    private final int pullCoinsCountMax = 4; //Максимальное количество монет, которые могут быть сгенерированы подряд

    Integer countCoins = -1; //Счетчик количества монет, которые запросили отрисовать.
    Integer countThorn = -1;
    Integer countLongThorn = -1;
    Integer countBird = -1;
    Integer countShield = -1;
    Integer countMagnet = -1;
    int distanceBirdAdd = 20000;
    boolean birdAdd = false;

    private final LinkedHashMap<Integer, Boolean> longThornAdded; //Дистацния и статус замены коротких шипов на длинные

    private final LinkedHashMap<Integer, Integer> distanceSpeed; //Дистация и скорость игры на данной дистации

    private boolean gameObjectLoaded = false;
    private ArrayList<Thorn> thorns;
    private ArrayList<Coin> coins;

    public GamePlayManager(Activity activity, DisplayMetrics displayMetrics, User user) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.user = user;
        gamePlayMenu = new GamePlayMenu(activity, displayMetrics, gameStatus);

        backGround = new BackGround(activity, displayMetrics, R.drawable.game_bg); //В дальнейшем нужно будет доработать, тк фон для разных уровней может быть разным.
        gearWheel = new GearWheel(activity, displayMetrics);

        //Получаем изображение шарика, которое выбрал пользователь.
        switch (user.getSelectAirBalloon()) {
            case 1:
                image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.air_balloon_1);
                break;
            case 2:
                image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.air_balloon_2);
                break;
            case 3:
                image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.air_balloon_3);
                break;
        }

        //Создаем объект шарика исходя из полученного id выбранного шарика пользователем.
        airBalloon = new AirBalloonObject(activity, displayMetrics, image);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textPaintEndGame = new Paint();
        textPaintEndGame.setColor(Color.parseColor("#800000"));
        textPaintEndGame.setTextSize(150);
        textPaintEndGame.setTextAlign(Paint.Align.CENTER);
        // Устанавливаем стиль текста
        textPaintEndGame.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)); // Устанавливаем жирный
        textPaintEndGame.setTextSkewX(-0.25f); //Наклон текста

        textPaintDistance = new Paint();
        textPaintDistance.setColor(Color.BLACK);
        textPaintDistance.setTextSize(70);
        textPaintDistance.setTextAlign(Paint.Align.RIGHT);
        // Устанавливаем стиль текста
        textPaintDistance.setTextSkewX(-0.25f); //Наклон текста

        //Устанавлияем первое время ускорения.
        currentTime =  LocalTime.now().plusSeconds(speedUpInterval);

        //Добавляем мелодию
        mediaPlayer = MediaPlayer.create(activity, R.raw.game_play_music);

        gameStatus = GameStatus.GAME;

        //Добавляем объект рекламы
//        rewardedAdActivity = new RewardedAdActivity();


        //Все что относится к генерации
        objectsGeneration =  new ObjectsGeneration(activity, displayMetrics, getAirBalloon());
        usedObjects = objectsGeneration.getUsedObjects();

        //Устанавливаем дистанцию и скорость на этой дистанции
        distanceSpeed = new LinkedHashMap<>();
        distanceSpeed.put(140000, 25);
        distanceSpeed.put(110000, 24);
        distanceSpeed.put(80000, 23);
        distanceSpeed.put(60000, 22);
        distanceSpeed.put(40000, 21);
        distanceSpeed.put(20000, 20);

        longThornAdded = new LinkedHashMap<>();
        longThornAdded.put(150000, false);
        longThornAdded.put(80000, false);
        longThornAdded.put(30000, false);

        thorns = new ArrayList<>();
        coins = new ArrayList<>();
    }

    public void drawAirBalloon(Canvas canvas) {
        airBalloon.draw(canvas);
    }

    public void drawCountCoins(Canvas canvas, DisplayMetrics displayMetrics) {
        canvas.drawText("Монеты: " + airBalloon.getCollectedCoins()
                , (int) (displayMetrics.heightPixels * 0.35)
                , (int) (displayMetrics.heightPixels * 0.1), textPaint);
    }

    public void drawHp(Canvas canvas, DisplayMetrics displayMetrics) {
        canvas.drawText("Здоровье: " + airBalloon.getHp()
                , (int) (displayMetrics.heightPixels * 0.35)
                , (int) (displayMetrics.heightPixels * 0.15), textPaint);
    }

    public void drawDistance(Canvas canvas, DisplayMetrics displayMetrics) {
        distance += speed;

        canvas.drawText("Высота: " + (distance / 100)
                , (int) (displayMetrics.heightPixels * 0.35)
                , (int) (displayMetrics.heightPixels * 0.2), textPaint);
    }

    public void drawGameOver(Canvas canvas, DisplayMetrics displayMetrics) {

        canvas.save();

        // Поворачиваем Canvas на 45 градусов
        canvas.rotate(-5, (int) (displayMetrics.heightPixels * 0.26),
                (int) (displayMetrics.widthPixels * 0.64));

        canvas.drawText("Конец игры !"
                , (int) (displayMetrics.widthPixels * 0.5)
                , (int) (displayMetrics.heightPixels * 0.4), textPaintEndGame);


        canvas.drawText("Набранная высота: " + (distance / 100)
                , (int) (displayMetrics.widthPixels * 0.73)
                , (int) (displayMetrics.heightPixels * 0.45), textPaintDistance);

        long maxDistance;

        if(distance / 100 > user.getMaxDistanceLevelFirst()) {
            maxDistance = distance / 100;
        } else {
            maxDistance = user.getMaxDistanceLevelFirst();
        }

        canvas.drawText("Рекорд высоты: " + maxDistance
                , (int) (displayMetrics.widthPixels * 0.73)
                , (int) (displayMetrics.heightPixels * 0.49), textPaintDistance);

        canvas.restore();
    }

    public void drawGamePlayMenu(Canvas canvas) {
        gamePlayMenu.drawMenuButtons(canvas);
    }

    public void drawMenuEnd(Canvas canvas) {
        gamePlayMenu.drawMenuEnd(canvas);
    }

    public void drawBackGround(Canvas canvas) {
        backGround.drawBackgroundImage(canvas, speed);
    }

    public void drawGearWheel(Canvas canvas) {
        gearWheel.drawGearWheel(canvas);
    }

    public boolean onTouchGearWheel(MotionEvent event) {
        return gearWheel.onTouch(event);
    }



    public GamePlayMenu getGamePlayMenu() {
        return gamePlayMenu;
    }

    public void speedUp() {
        for (Map.Entry<Integer, Integer> entry : distanceSpeed.entrySet()) {
            if(distance > entry.getKey()) {
                speed = entry.getValue();
                break;
            }
        }
    } //Устаналиваем скорость в зависимости от дистанции

    private boolean determineReplaceThorn() {
        for (Map.Entry<Integer, Boolean> entry : longThornAdded.entrySet()) {
            if(!entry.getValue() && distance > entry.getKey()) {
                entry.setValue(true); //Запоминаем, что на этом чекпоинте мы уже меняли шип
                return true;
            }
        }

        return false;
    } //В зависимости от пройденной дистанции определяет, нужно ли заменить маленький шип на большой


    public int getHpAirBalloon() {
        return airBalloon.getHp();
    }

    public boolean onTouchAirBalloon(MotionEvent event) {
        return airBalloon.onTouch(event);
    }

    public void startMusic() {
        mediaPlayer.start();
    }

    public void releaseMusic() {
        mediaPlayer.release();
    }

    public int getCollectedCoins() {
        return airBalloon.getCollectedCoins();
    }

    public int getDistance() {
        return distance;
    }

    public AirBalloonObject getAirBalloon() {
        return airBalloon;
    }

    public static int getNewDistanceAdditionObject(int minDistanceAdditionObject, int maxDistanceAdditionObject, int distance) {
        return (int) (minDistanceAdditionObject + Math.random() * (maxDistanceAdditionObject - minDistanceAdditionObject)) + distance;
    } // Генерируем новую дистацию для добавления объекта в пул

    public void restartAirballoon() {
        airBalloon.restartAirBalloon();
    } //Устанавливает дефолтные параметры для шарика

    public void restartSpeed() {
        speed = initialSpeed;
    } //Устанавливает дефолтную скорость игры

    public void restartDistance() {
        distance = 0;
    }

    public void restartCoins() {
        airBalloon.resetCoins();
    }

    public void restartGeneration() {
        objectsGeneration =  new ObjectsGeneration(activity, displayMetrics, getAirBalloon());
        usedObjects = objectsGeneration.getUsedObjects();

        countCoins = -1;
        countThorn = -1;
        countLongThorn = -1;
        countBird = -1;
        countShield = -1;

        distanceAdditionObject = 35;
        birdAdd = false;

        //Обнуляем статусы замен коротких шипов на длинные
        for (Map.Entry<Integer, Boolean> entry : longThornAdded.entrySet()) {
            if(entry.getValue()) {
                entry.setValue(false);
            }
        }

        airBalloon.removeShield();
    }

    public void switchStatusGame(Boolean isPaused) {
        ArrayList<Object> objects = usedObjects.get(3).getObjects();
        if(objects.isEmpty()) {
            return;
        }

        //Останавливаем или запускаем анимацию птиц в завивисмости от статуса паузы
        if(isPaused) {
            for(int i = 0; i < objects.size(); i++) {
                Bird bird = (Bird) objects.get(i);
                bird.stopAnimation();
            }
        } else {
            for(int i = 0; i < objects.size(); i++) {
                Bird bird = (Bird) objects.get(i);
                bird.startAnimation();
            }
        }
    }

    public void restartBackGround() {
        backGround.restartBackground();
    }

    //Генерация объектов для бесконечной игры
    public void startObjectsGeneration(Canvas canvas) {
        if(objectsGeneration.checkAvailabilityCoins() && objectsGeneration.checkAvailabilityThorns()) { //Доступны все объекты

            //Определяем что нужно добавить в пул объектов на отрисовку.
            if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
                int b = random.nextInt(4); //Случайно выбираем, что будем добавлять в пул

                if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                    countCoins++; //Добавляем монетку в пул
                    pullCoinsCount++;
                    distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                    countThorn++; //Добавляем шип в пул
                    distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject), (maxDistanceAdditionObject), distance);
                    pullCoinsCount = 0;
                } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                    countLongThorn++; //Добавляем шип в пул
                    distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (int) (maxDistanceAdditionObject * 1.4), distance);
                    pullCoinsCount = 0;
                } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird) {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (int) (maxDistanceAdditionObject * 1.4), distance);
                    pullCoinsCount = 0;
                } else if (objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield) {
                    countShield++; //Добавляем щит в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                    pullCoinsCount = 0;
                } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet) {
                    countMagnet++; //Добавляем магнит в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                    pullCoinsCount = 0;
                }else {
                    //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                    if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                        countCoins++; //Добавляем монетку в пул
                        distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                    } else {
                        countThorn++; //Добавляем шип в пул если нет монет
                        distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (maxDistanceAdditionObject * 2), distance);
                    }
                }

            }

            //Отрисовываем монетки из пула
            needZeroCoins = usedObjects.get(0).drawObjects(canvas, countCoins, "coin");

            if (needZeroCoins) { //Обнуляем счетчик монет
                countCoins = -1;

                //Определяем новую позицию для монет
                ArrayList<Object> coins = usedObjects.get(0).getObjects();

                for (Object ob : coins) {
                    Coin coin = (Coin) ob;
                    coin.calculateStartPosition();
                }
            }

            //Отрисовываем шипы из пула
            needZeroThorn = usedObjects.get(1).drawObjects(canvas, countThorn, "thorn");

            if (needZeroThorn) {
                countThorn = -1;

                ArrayList<Object> thorns = usedObjects.get(1).getObjects();

                //Если нужно, меняем маленький шип на длинный
                if(determineReplaceThorn()) {
                    usedObjects.get(1).removeObject();
                    usedObjects.get(2).addObject();
                }

                for (Object ob : thorns) {
                    Thorn thorn = (Thorn) ob;
                    thorn.calculateStartPosition();
                }
            }

            //Отрисовываем длинные шипы из пула
            needZeroLongThorn = usedObjects.get(2).drawObjects(canvas, countLongThorn, "long_thorn");

            if (needZeroLongThorn) {
                countLongThorn = -1;

                ArrayList<Object> longThorns = usedObjects.get(2).getObjects();

                for (Object ob : longThorns) {
                    LongThorn longThorn = (LongThorn) ob;
                    longThorn.calculateStartPosition();
                }
            }

            //Добавляем птицу в пул если нужно
            if(distance >= distanceBirdAdd && !birdAdd) {
                usedObjects.get(3).addObject(); //Добавляем в пул объектов для отрисовки птицу
                birdAdd = true;
            }

            //Отрисовываем птиц из пула
            needZeroBird = usedObjects.get(3).drawObjects(canvas, countBird, "bird");

            if (needZeroBird) {
                countBird = -1;

                ArrayList<Object> birds = usedObjects.get(3).getObjects();

                for (Object ob : birds) {
                    Bird bird = (Bird) ob;
                    bird.calculateStartPosition();
                }
            }

            //Отрисовываем щиты из пула
            needZeroShield = usedObjects.get(4).drawObjects(canvas, countShield, "shield");

            if (needZeroShield) {
                countShield = -1;

                ArrayList<Object> shields = usedObjects.get(4).getObjects();

                for (Object ob : shields) {
                    Shield shield = (Shield) ob;
                    shield.calculateStartPosition();
                }
            }

            //Отрисовываем магниты из пула
            needZeroMagnet = usedObjects.get(5).drawObjects(canvas, countMagnet, "magnet");

            if (needZeroMagnet) {
                countMagnet = -1;

                ArrayList<Object> magnets = usedObjects.get(5).getObjects();

                for (Object ob : magnets) {
                    Magnet magnet = (Magnet) ob;
                    magnet.calculateStartPosition();
                }
            }
        }
    }

    //Генерация объектов первого уровня.
    public void startObjectsGenerationLevel1(Canvas canvas) {
        loadObjectsLevel1();

        for(Thorn thorn: thorns) {
            thorn.drawThorn(canvas);

            //Удаляем ненужные шипы
            if (thorn.getYPosition() > displayMetrics.heightPixels) {
                thorns.remove(thorn);
            }
        }

        for(Coin coin: coins) {
            coin.draw(canvas);

            //Удаляем ненужные монеты
            if (coin.getYPosition() > displayMetrics.heightPixels) {
                coins.remove(coin);
            }
        }
    }

    //Загружаем все объекты для первого уровня
    public void loadObjectsLevel1() {

        if(!gameObjectLoaded) {
            //Загружаем объекты
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -500));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.CENTER, -1000));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT2, -1300));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT1, -1650));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT2, -1700));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.CENTER, -2000));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -2200));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -2700));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT2, -2700));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.CENTER, -3100));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT1, -3200));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT3, -3400));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -3600));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT3, -3750));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT2, -4000));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.CENTER, -4100));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT3, -4350));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -4600));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT4, -4850));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT2, -5100));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT1, -5200));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT3, -5300));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT2, -5500));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT3, -6000));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -6100));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.CENTER, -6500));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT2, -7000));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT2, -7250));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT1, -7500));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.CENTER, -7800));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -8000));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -8300));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.CENTER, -8600));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT, -8750));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -8900));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT2, -9300));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -9900));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT1, -10400));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.CENTER, -10400));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT3, -10700));
            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.RIGHT1, -10900));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -11000));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT4, -11350));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT, -11500));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.RIGHT1, -12000));

            coins.add(new Coin(activity, displayMetrics, airBalloon, PosX.LEFT2, -12200));

            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.CENTER, -12600));
            thorns.add(new Thorn(activity, displayMetrics, airBalloon, PosX.LEFT2, -12950));

            gameObjectLoaded = true;
            speed = 13;
        }
    }
}
