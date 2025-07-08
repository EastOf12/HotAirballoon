package com.example.airballoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.levels.FreeLevel;
import com.example.airballoon.levels.Level1;

public class GamePlayActivity extends AppCompatActivity {
    int selectedLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int selectedLevel = intent.getIntExtra("levelNumber", 0);

        System.out.println("selectedLevel" + selectedLevel);

        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Запускаем уровень

        switch (selectedLevel) {
            case 1:
                Level1 level1 = new Level1(this);
                setContentView(level1);
                level1.start();
                break;
        }

        //Бесконечная игра
//        if(selectedLevel != 1) {
//            FreeLevel freeLevel = new FreeLevel(this);
//            setContentView(freeLevel);
//            freeLevel.start();
//        }
    }
}
