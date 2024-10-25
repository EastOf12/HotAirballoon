package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.airballoon.Managers.GamePlayManager;
import com.example.airballoon.managers.ManagerFPS;
import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;

//Шаблон уровня
@SuppressLint("ViewConstructor")
abstract class BaseLevel extends SurfaceView implements Runnable{

    //Основные классы
    Activity activity;
    protected final SurfaceHolder surfaceHolder; //Используется для управлением поверхностью на которой происходит отрисовка
    DisplayMetrics displayMetrics;
    ManagerFPS managerFPS;
    GamePlayManager gamePlayManager;
    User user;

    //Поля состояний (В целом можно когда-нибудь вынести в отдельный класс)
    protected volatile boolean running = false;
    protected boolean isPaused = false;
    int startSpeed = 15;
    boolean needSave = true;

    @SuppressLint("UseCompatLoadingForDrawables")
    public BaseLevel(Activity activity) {
        super(activity);
        this.activity = activity;
        surfaceHolder = getHolder();
        user = SaveManager.readFromFile(activity);
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        managerFPS = new ManagerFPS();
        gamePlayManager = new GamePlayManager(activity, displayMetrics, user);
    }

    //Запускаем уровень
    public void start() {
        running = true;
        GamePlayManager.speed = startSpeed;
        Thread gameThread = new Thread(this);
        gameThread.start();
    }


    //Меняем статус игры
    public void switchGameStatus() {
        isPaused = !isPaused;

        if(isPaused) {
            GamePlayManager.speed = 0;
        } else {
            GamePlayManager.speed = 15;
        }
    }
}
