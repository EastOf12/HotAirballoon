package com.example.airballoon.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.concurrent.TimeUnit;

public class GamePlayManager {
    Activity activity;
    DisplayMetrics displayMetrics;
    ArrayList<Coin> coins;
    ArrayList<Thorn> thorns;
    Paint textPaint;
    public static int speed = 15;
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
    private int endDistance;

    //Все что относится к генерации
    private ObjectsGeneration objectsGeneration;
    private final ArrayList<Wrapper> usedObjects;
    private LocalDateTime oldTimeGenerate;
    private LocalDateTime newTimeGenerate;
    boolean needZeroCoins;
    boolean needZeroThorn;

    Integer countCoins = 0; //Счетчик количества монет, которые запросили отрисовать.
    Integer countThorn = 0 ;

    public GamePlayManager(Activity activity, DisplayMetrics displayMetrics, User user) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
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
        generateCoins();
        generateThorns();
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

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

        oldTimeGenerate = LocalDateTime.now();
        newTimeGenerate = LocalDateTime.now().plusSeconds(3);
    }

    public void startObjectsGeneration(Canvas canvas) {
        if(objectsGeneration.checkAvailabilityCoins() && objectsGeneration.checkAvailabilityThorns()) { //Доступны все объекты


            //Определяем, что нужно добавить на отрисовку еще один объект Wrapper
            if (newTimeGenerate.isBefore(oldTimeGenerate)) { //Запоминаем, что пришло время рисовать еще один объект
//                Время через которое нужно будет снова отобразить объект.

                if (objectsGeneration.getUsedObjects().get(0).getDrawCount() > countCoins) {
                    countCoins++; //Добавляем монетку в игровую итерацию
                    newTimeGenerate = generateTime(0.3, 2.0);
                } else if (objectsGeneration.getUsedObjects().get(1).getDrawCount() > countThorn) {
                    countThorn++;
                    newTimeGenerate = generateTime(1.0, 2.0);
                }

            }

            oldTimeGenerate = LocalDateTime.now();

            //Рисуем все монетки на отрисовку
            needZeroCoins = usedObjects.get(0).drawObjects(canvas, countCoins, "coin");

            if (needZeroCoins) { //Обнуляем счетчик монет

                countCoins = 0;

                //Определяем новую позицию для монет
                ArrayList<Object> coins = usedObjects.get(0).getObjects();

                for (Object ob : coins) {
                    Coin coin = (Coin) ob;
                    coin.calculateStartPosition();
                }
            }

            //Рисуем все шипы на отрисовку
            needZeroThorn = usedObjects.get(1).drawObjects(canvas, countThorn, "thorn");

            if (needZeroThorn) {
                countThorn = 0;

                ArrayList<Object> thorns = usedObjects.get(1).getObjects();

                for (Object ob : thorns) {
                    Thorn thorn = (Thorn) ob;
                    thorn.calculateStartPosition();
                }
            }
        }
    }


    public void generateCoins() {
        int yPosition = random.nextInt(2500) - 3500;

        for (int i = 0; i <= 5; i++) {
            Coin coin = new Coin(activity, displayMetrics, airBalloon);

            yPosition += random.nextInt(500);

            coin.setYPosition(yPosition);
            coins.add(coin);
        }
    }

    public void generateThorns() {
        int yPosition = random.nextInt(2500) - 3500;
        for (int i = 0; i <= 3; i++) {
            Thorn thorn = new Thorn(activity, displayMetrics, airBalloon);
            yPosition += random.nextInt(500);
            thorn.setYPosition(yPosition);
            thorns.add(thorn);
        }
    }

    public void drawAirBalloon(Canvas canvas) {
        airBalloon.draw(canvas);
    }

    public void drawCoins(Canvas canvas) {
        for (Coin coin : coins) {
            coin.draw(canvas);
        }
    }

    public void drawThorn(Canvas canvas) {
        for (Thorn thorn : thorns) {
            thorn.drawThorn(canvas);
        }
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

    public void drawGamePlayMenu(Canvas canvas) {
        gamePlayMenu.drawMenuButtons(canvas);
    }

    public void drawMenuEndLose(Canvas canvas) {
        gamePlayMenu.drawMenuEndLose(canvas);
    }

    public void drawMenuEndFinish(Canvas canvas) {
        gamePlayMenu.drawMenuEndFinish(canvas);
    }

    public void drawBackGround(Canvas canvas) {
        backGround.drawBackgroundImage(canvas);
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

    public boolean levelEnd() {
        if(endDistance <= (distance / 100)) {
            return true;
        }
        return false;
    }

    public void setEndDistance(int endDistance) {
        this.endDistance = endDistance;
    }

    public void drawLevelEnd() {
        System.out.println("Рисуем конец уровня");
    }

    public AirBalloonObject getAirBalloon() {
        return airBalloon;
    }

    public static LocalDateTime generateTime(double minSeconds, double maxSeconds) {
        // Генерируем случайное значение времени в заданных пределах
        double randomSeconds = minSeconds + Math.random() * (maxSeconds - minSeconds);

        // Генерируем новое время
        LocalDateTime newTimeGenerate = LocalDateTime.now().plusSeconds((long) randomSeconds);

        return newTimeGenerate;
    }
}
