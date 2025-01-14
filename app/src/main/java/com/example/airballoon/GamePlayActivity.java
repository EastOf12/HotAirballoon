package com.example.airballoon;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.levels.Level1;
import com.example.airballoon.levels.Level2;

public class GamePlayActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Тут запускается нужный уровень


        //Старый вариант
//        GameView gameView = new GameView(this);
//        setContentView(gameView);
//
//        gameView.start();


        //Новый вариант
        Level1 level1 = new Level1(this);
        setContentView(level1);
        level1.start();
    }
}
