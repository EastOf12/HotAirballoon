package com.example.airballoon.managers;

import static com.example.airballoon.managers.LevelDistanceInfo.addMagnetCounter;
import static com.example.airballoon.managers.LevelDistanceInfo.addShieldCounter;
import static com.example.airballoon.managers.LevelDistanceInfo.getBirdStartDistance;
import static com.example.airballoon.managers.LevelDistanceInfo.getLongThornDistance;
import static com.example.airballoon.managers.LevelDistanceInfo.getMagnetStartDistance;
import static com.example.airballoon.managers.LevelDistanceInfo.getShieldStartDistance;
import static com.example.airballoon.managers.LevelDistanceInfo.hadMagnet;
import static com.example.airballoon.managers.LevelDistanceInfo.hadShield;
import static com.example.airballoon.managers.LevelDistanceInfo.resetCounter;

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
import com.example.airballoon.game_objects.BaseObject;
import com.example.airballoon.game_objects.Bird;
import com.example.airballoon.game_objects.Coin;
import com.example.airballoon.game_objects.GamePlayMenu;
import com.example.airballoon.game_objects.GearWheel;
import com.example.airballoon.game_objects.LongThorn;
import com.example.airballoon.game_objects.Magnet;
import com.example.airballoon.game_objects.PosX;
import com.example.airballoon.game_objects.Shield;
import com.example.airballoon.game_objects.Star;
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

    public static int speed = 0; //Стартовая скорость

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
    private final SoundManager soundManager;
    boolean needZeroCoins = true;
    boolean needZeroThorn;
    boolean needZeroLongThorn;
    boolean needZeroBird;
    boolean needZeroShield;
    boolean needZeroMagnet;
    boolean needZeroStar;
    int shieldMinDistanceAddition = 300_00;
    int shieldDistanceAddition = 0;
    boolean needRemoveAllShield = false;
    private int minDistanceAdditionObject = 250; //Минимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int maxDistanceAdditionObject = 450; //Максимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int distanceAdditionObject = 35; //Дистацния при достижении которой добавляем новый объект в пул
    private int pullCoinsCount = 0; //Число монеток подряд добавленных в пул.
    private final int pullCoinsCountMax = 2; //Максимальное количество монет + 1, которые могут быть сгенерированы подряд, кроме случаев обновления пула

    Integer countCoins = -1; //Счетчик количества монет, которые запросили отрисовать.
    Integer countThorn = -1;
    Integer countLongThorn = -1;
    Integer countBird = -1;
    Integer countShield = -1;
    Integer countMagnet = -1;
    Integer countStar = -1;
    int distanceBirdAdd = 20000;
    boolean birdAdd = true;

    private final LinkedHashMap<Integer, Boolean> longThornAdded; //Дистацния и статус замены коротких шипов на длинные

    private final HashMap<Integer, Integer> distanceSpeed; //Дистация и скорость игры на данной дистации

    private int starsAdded = 0;
    private boolean needStar = false;
    LevelProgressManager levelProgressManager;
    Coins coinsCount;
    private final int levelNum;
    private final Bird bird;

    public GamePlayManager(Activity activity, DisplayMetrics displayMetrics, User user, int levelNum) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.user = user;
        gamePlayMenu = new GamePlayMenu(activity, displayMetrics, gameStatus);
        soundManager = new SoundManager(activity);

        if(levelNum > 0) {
            levelProgressManager = new LevelProgressManager(activity, displayMetrics, levelNum);
        }

        this.levelNum = levelNum;

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
        airBalloon = new AirBalloonObject(activity, displayMetrics, image, soundManager);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setTextSkewX(-0.25f); //Наклон текста
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
        textPaintDistance.setTextSize(60);
        textPaintDistance.setTextAlign(Paint.Align.RIGHT);
        // Устанавливаем стиль текста
//        textPaintDistance.setTextSkewX(-0.25f); //Наклон текста

        //Устанавлияем первое время ускорения.
        currentTime =  LocalTime.now().plusSeconds(speedUpInterval);

        //Добавляем мелодию
        mediaPlayer = MediaPlayer.create(activity, R.raw.game_play_music);

        gameStatus = GameStatus.GAME;

        //Добавляем объект рекламы
//        rewardedAdActivity = new RewardedAdActivity();


        //Все что относится к генерации
        objectsGeneration =  new ObjectsGeneration(activity, displayMetrics, getAirBalloon());
        setMaxBirds(LevelDistanceInfo.getMaxBirdCount(levelNum));
        usedObjects = objectsGeneration.getUsedObjects();

        //Устанавливаем дистанцию и скорость на этой дистанции
        distanceSpeed = LevelDistanceInfo.getDistanceSpeeds(levelNum);

        longThornAdded = getLongThornDistance(levelNum);

        coinsCount = new Coins(activity, displayMetrics, R.drawable.coins, 0.1);

        //Уровни где птичка будет не нужна
        if(levelNum == 1) {
            birdAdd = false;
        }

        bird = new Bird(activity, displayMetrics, getAirBalloon());

        resetCounter();
    }

    public void drawAirBalloon(Canvas canvas) {
        airBalloon.draw(canvas);
    }

    public void drawCountCoins(Canvas canvas, DisplayMetrics displayMetrics) {
        canvas.drawText(String.valueOf(airBalloon.getCollectedCoins())
                , (int) (displayMetrics.widthPixels * 0.92)
                , (int) (displayMetrics.heightPixels * 0.1), textPaint);

        coinsCount.draw(canvas);

    }

    public void countDistance() {
        distance += speed;
    }

    private void drawDistanceLevelFree(Canvas canvas) {
        canvas.drawText("Высота: " + (distance / 100)
                , (int) (displayMetrics.widthPixels * 0.93)
                , (int) (displayMetrics.heightPixels * 0.15), textPaintDistance);

        long maxDistance;

        if(distance / 100 > user.getMaxDistanceLevelFirst()) {
            maxDistance = distance / 100;
        } else {
            maxDistance = user.getMaxDistanceLevelFirst();
        }

        canvas.drawText("Рекорд: " + maxDistance
                , (int) (displayMetrics.widthPixels * 0.93)
                , (int) (displayMetrics.heightPixels * 0.2), textPaintDistance);
    }


    public void drawGameOver(Canvas canvas, DisplayMetrics displayMetrics, int selectedLevel) {
        boolean freeLevel = selectedLevel == 0;
        double heightPixels = 0.4;

        if(!freeLevel) {
            heightPixels = 0.5;
        }


        canvas.save();

        // Поворачиваем Canvas на 45 градусов
        canvas.rotate(-5, (int) (displayMetrics.heightPixels * 0.26),
                (int) (displayMetrics.widthPixels * 0.64));

        String textEndGame = "Конец игры !";

        canvas.drawText(textEndGame
                , (int) (displayMetrics.widthPixels * 0.5)
                , (int) (displayMetrics.heightPixels * heightPixels), textPaintEndGame);

        if(freeLevel) {
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
    }

    public void drawLevelCompleted(Canvas canvas, DisplayMetrics displayMetrics, int starCount) {
        soundManager.levelCompleted();
        gamePlayMenu.drawLevelCompleted(canvas, starCount);
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

    public void drawLevelProgress(Canvas canvas) {
        if(levelNum > 0) {
            levelProgressManager.run(canvas, distance);
        } else {
            drawDistanceLevelFree(canvas);
        }
    } //Выводит информацию по прогрессу уровня.



    public GamePlayMenu getGamePlayMenu() {
        return gamePlayMenu;
    }

    public void speedUp() {
        for (Map.Entry<Integer, Integer> entry : distanceSpeed.entrySet()) {
            if(distance >= entry.getKey() && speed < entry.getValue()) {
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
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.start();
    }

    public void restartMusic() {
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
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
        birdAdd = true;

        //Обнуляем статусы замен коротких шипов на длинные
        for (Map.Entry<Integer, Boolean> entry : longThornAdded.entrySet()) {
            if(entry.getValue()) {
                entry.setValue(false);
            }
        }

        airBalloon.removeShield();
    }

    public void switchStatusGame(Boolean isPaused) {
        soundManager.pause();

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

    public boolean checkLevelProgress(int needDistance) {
        return distance >= needDistance * 100;
    } //Возвращает ответ, можно ли считать уровень пройденным.

    public void restartBackGround() {
        backGround.restartBackground();
    }

    //Генерация объектов для бесконечной игры
    public void startObjectsGeneration(Canvas canvas, int levelNum) {
        if(objectsGeneration.checkAvailabilityCoins() && objectsGeneration.checkAvailabilityThorns()) { //Доступны все объекты
            addObjects(levelNum);
            drawObjects(canvas);
        }
    }

    private void addObjects(int levelNum) {
        switch (levelNum) {
            case 0:
                addObjectsLevelFree();
                break;
            case 1:
                addObjectsLevel1();
                break;
            case 2:
                addObjectsLevel2();
                break;
            case 3:
                addObjectsLevel3();
                break;
            case 4:
                addObjectsLevel4();
                break;
            case 5:
                addObjectsLevel5();
                break;
            case 6:
                addObjectsLevel6();
                break;
            case 7:
                addObjectsLevel7();
                break;
            case 8:
                addObjectsLevel8();
                break;
            case 9:
                addObjectsLevel9();
                break;
            case 10:
                addObjectsLevel10();
                break;
            case 11:
                addObjectsLevel11();
                break;
            case 12:
                addObjectsLevel12();
                break;
            case 13:
                addObjectsLevel13();
                break;
            case 14:
                addObjectsLevel14();
                break;
            case 15:
                addObjectsLevel15();
                break;
            case 16:
                addObjectsLevel16();
                break;
        }
    }
    private void addObjectsLevelFree() {
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
                countLongThorn++; //Добавляем длинный шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (int) (maxDistanceAdditionObject * 1.4), distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (int) (maxDistanceAdditionObject * 1.4), distance);
                pullCoinsCount = 0;
            } else if (distance > shieldDistanceAddition + shieldMinDistanceAddition &&
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield) {
                countShield++; //Добавляем щит в пул
                shieldDistanceAddition = distance;
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
    }
    private void addObjectsLevel1() {

        //Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(4); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countThorn++; //Добавляем шип в пул если нет монет
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject")),
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject")),
                            distance);
                }
            }
        }
    }
    private void addObjectsLevel2() {
        //Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(4); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countThorn++; //Добавляем шип в пул если нет монет
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject")),
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject")),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel3() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(5); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                countThorn++; //Добавляем шип в пул если нет монет
                distanceAdditionObject = getNewDistanceAdditionObject(
                        (LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject")),
                        (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject")),
                        distance);
            }
        }
    }

    private void addObjectsLevel4() {
        //Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(4); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum) ) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countThorn++; //Добавляем шип в пул если нет монет
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject")),
                            (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject")),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel5() {
        //Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(4); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel6() {
        //Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(5); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            }  else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            }else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            }else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel7() {
//Определяем что нужно добавить в пул объектов на отрисовку.
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(5); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            }else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel8() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(5); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                            (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                            distance);
                } else {
                    countThorn++; //Добавляем шип в пул если нет монет
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                            (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel9() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(5); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel10() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(6); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel11() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(7); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel12() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(8); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
                            && distance > shieldDistanceAddition + shieldMinDistanceAddition
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                shieldDistanceAddition = distance;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel13() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(7); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
                            && distance > shieldDistanceAddition + shieldMinDistanceAddition
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                shieldDistanceAddition = distance;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel14() {

        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(7); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
                            && distance > shieldDistanceAddition + shieldMinDistanceAddition
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                shieldDistanceAddition = distance;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel15() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(8); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
                            && distance > shieldDistanceAddition + shieldMinDistanceAddition
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                shieldDistanceAddition = distance;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void addObjectsLevel16() {
        if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
            int b = random.nextInt(7); //Случайно выбираем, что будем добавлять в пул

            if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                countCoins++; //Добавляем монетку в пул
                pullCoinsCount++;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
            } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                countThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (
                    objectsGeneration.getUsedObjects().get(4).getDrawCount() > countShield
                            && distance > getShieldStartDistance(levelNum) && hadShield(levelNum)
                            && distance > shieldDistanceAddition + shieldMinDistanceAddition
            ) {
                countShield++; //Добавляем щит в пул
                addShieldCounter();
                shieldDistanceAddition = distance;
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(3).getDrawCount() > countBird && distance > getBirdStartDistance(levelNum)) {
                countBird++; //Добавляем птичку в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(5).getDrawCount() > countMagnet
                    && distance > getMagnetStartDistance(levelNum) && hadMagnet(levelNum)) {
                countMagnet++; //Добавляем магнит в пул
                addMagnetCounter();
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                        distance);
                pullCoinsCount = 0;
            } else if (objectsGeneration.getUsedObjects().get(2).getDrawCount() > countLongThorn) {
                countLongThorn++; //Добавляем шип в пул
                distanceAdditionObject = getNewDistanceAdditionObject(
                        LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject") * 2,
                        (int) (LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject") * 1.5),
                        distance);
                pullCoinsCount = 0;
            } else {
                //Нет того элемента, который хотели отрисовать, рисуем, что осталось
                if(objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                } else {
                    countBird++; //Добавляем птичку в пул
                    distanceAdditionObject = getNewDistanceAdditionObject(
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("minDistanceAdditionObject"),
                            LevelDistanceInfo.getDistanceAddition(levelNum).get("maxDistanceAdditionObject"),
                            distance);
                }
            }
        }
    }

    private void drawObjects(Canvas canvas) {
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
        if(distance >= distanceBirdAdd && birdAdd) {
            usedObjects.get(3).addObject(bird); //Добавляем в пул объектов для отрисовки птицу
            birdAdd = false;
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

        //Отрисовываем магниты из пула
        needZeroStar = usedObjects.get(6).drawObjects(canvas, countStar, "star");

        if (needZeroStar) {
            countStar = -1;

            ArrayList<Object> stars = usedObjects.get(6).getObjects();

            for (Object ob : stars) {
                Star star = (Star) ob;
                star.calculateStartPosition();
            }
        }
    }

    private void addStar(int[] distanceAdd) {
        if(starsAdded == 0 && distance >= distanceAdd[0]) {
            needStar = true;
            starsAdded = 1;
        } else if(starsAdded == 1 && distance >= distanceAdd[1]) {
            needStar = true;
            starsAdded = 2;
        } else if(starsAdded == 2 && distance >= distanceAdd[2]) {
            needStar = true;
            starsAdded = 3;
        }

        if(needStar) {
            countStar++;
            needStar = false;
        }

    } //Принимает массив значений, когда нужно добавить звезду в пул на отрисовку

    private void setMaxBirds(int maxCount) {
        objectsGeneration.setMaxBirds(maxCount);
    }
}

class Coins extends BaseObject {
    public Coins(Activity activity, DisplayMetrics displayMetrics, int resourceId, double percentage) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions((int) ((displayMetrics.widthPixels * 0.75)),
                (int) ((displayMetrics.heightPixels * 0.065)));
    }
}
