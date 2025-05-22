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
import com.example.airballoon.game_objects.Coin;
import com.example.airballoon.game_objects.GamePlayMenu;
import com.example.airballoon.game_objects.GearWheel;
import com.example.airballoon.game_objects.Thorn;
import com.example.airballoon.R;
import com.example.airballoon.game_objects.Wrapper;
import com.example.airballoon.models.AirBalloon;
import com.example.airballoon.models.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

public class GamePlayManager {
    Activity activity;
    DisplayMetrics displayMetrics;
    ArrayList<Coin> coins;
    ArrayList<Thorn> thorns;
    Paint textPaint;
    Paint textPaintEndGame;
    Paint textPaintDistance;
    public static int speed = 15;
    public final int initialSpeed = 15;
    private final int boost = 3;
    GamePlayMenu gamePlayMenu;
    Random random = new Random();
    private final int speedUpInterval = 15;

    int distance = 0;
    LocalTime currentTime;

    AirBalloonObject airBalloon;
    BackGround backGround;
    GearWheel gearWheel;

    AirBalloon airBalloonInfo;
    MediaPlayer mediaPlayer;
    static GameStatus gameStatus;
    private User user;
    private Bitmap image;

    //Все что относится к генерации
    private ObjectsGeneration objectsGeneration;
    private ArrayList<Wrapper> usedObjects;
    boolean needZeroCoins;
    boolean needZeroThorn;
    private int minDistanceAdditionObject = 150; //Минимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int maxDistanceAdditionObject = 250; //Максимальная пройденная дистанция, после которой можно добавить новый объект в пул
    private int distanceAdditionObject = 35; //Дистацния при достижении которой добавляем новый объект в пул
    private int pullCoinsCount = 0; //Число монеток подряд добавленных в пул.
    private final int pullCoinsCountMax = 4; //Максимальное количество монет, которые могут быть сгенерированы подряд

    Integer countCoins = -1; //Счетчик количества монет, которые запросили отрисовать.
    Integer countThorn = -1;

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

        coins = new ArrayList<>();
        thorns = new ArrayList<>();
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
    }

    public void startObjectsGeneration(Canvas canvas) {
        if(objectsGeneration.checkAvailabilityCoins() && objectsGeneration.checkAvailabilityThorns()) { //Доступны все объекты

            //Определяем что нужно добавить в пул объектов на отрисовку.
            if (distance >= distanceAdditionObject) { //Проверяем, что дистанция на отрисовку достигнута
                int b = random.nextInt(4);; //Случайно выбираем, что будем добавлять в пул

                if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins && b < 3 && pullCoinsCount <= pullCoinsCountMax) {
                    countCoins++; //Добавляем монетку в пул
                    pullCoinsCount++;
                    distanceAdditionObject = getNewDistanceAdditionObject(minDistanceAdditionObject, maxDistanceAdditionObject, distance);
                } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                    countThorn++; //Добавляем шип в пул
                    distanceAdditionObject = getNewDistanceAdditionObject((minDistanceAdditionObject * 2), (maxDistanceAdditionObject * 2), distance);
                    pullCoinsCount = 0;
                } else {
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

                for (Object ob : thorns) {
                    Thorn thorn = (Thorn) ob;
                    thorn.calculateStartPosition();
                }
            }
        }
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
        if(LocalTime.now().isAfter(currentTime)) {
            speed += boost;
            currentTime =  LocalTime.now().plusSeconds(speedUpInterval);
        }
    }

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

        distanceAdditionObject = 35;
    }

    public void restartBackGround() {
        backGround.restartBackground();
    }
}
