package com.example.airballoon.Managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.exceptions.SaveException;
import com.example.airballoon.game_objects.AirBalloonObject;
import com.example.airballoon.game_objects.BackGround;
import com.example.airballoon.game_objects.Coin;
import com.example.airballoon.game_objects.GamePlayMenu;
import com.example.airballoon.game_objects.GearWheel;
import com.example.airballoon.game_objects.Thorn;
import com.example.airballoon.R;
import com.example.airballoon.models.AirBalloon;
import com.example.airballoon.models.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import com.example.airballoon.managers.GameStatus;

import com.google.gson.Gson;

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

    public GamePlayManager(Activity activity, DisplayMetrics displayMetrics, User user) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        gamePlayMenu = new GamePlayMenu(activity, displayMetrics, gameStatus);

        backGround = new BackGround(activity, displayMetrics, R.drawable.game_bg); //В дальнейшем нужно будет доработать, тк фон для разных уровней может быть разным.
        gearWheel = new GearWheel(activity, displayMetrics);

        //Получаем изображение шарика, которое выбрал пользователь.
        System.out.println("Будем рисовать шарик с id " + user.getSelectAirBalloon());
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

    public void drawMenuEnd(Canvas canvas) {
        gamePlayMenu.drawMenuEnd(canvas);
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

}
