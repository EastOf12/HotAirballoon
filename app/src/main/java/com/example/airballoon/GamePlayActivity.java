package com.example.airballoon;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.levels.FreeLevel;
import com.example.airballoon.managers.DataManager;
import com.example.airballoon.managers.SaveManager;

import java.time.LocalDateTime;

public class GamePlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int selectedLevel = intent.getIntExtra("levelNumber", 0);

        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Запускаем уровень
        FreeLevel freeLevel = new FreeLevel(this, selectedLevel);
        setContentView(freeLevel);
        freeLevel.start();
    }
}
