package com.example.airballoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.managers.DataManager;
import com.example.airballoon.managers.SaveManager;

public class LoadLevelActivity extends AppCompatActivity {
    private int selectedLevel;
    private int distance;
    private int coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedLevel = intent.getIntExtra("levelNumber", 0);
        distance = intent.getIntExtra("distance", 0);
        coins = intent.getIntExtra("coins", 0);


        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.load_level_activity);

        LevelStart levelStart = new LevelStart();
        levelStart.start(); // Запускаем поток
    }


    private void loadLevel() {
        boolean dataLoaded = DataManager.loadData(this, SaveManager.readFromFile(this), selectedLevel);

        if (dataLoaded) {
            // Если данные загружены успешно, переходим к игровому процессу
            Intent gamePlayIntent = new Intent(this, GamePlayActivity.class);
            gamePlayIntent.putExtra("levelNumber", selectedLevel);
            gamePlayIntent.putExtra("distance", distance);
            gamePlayIntent.putExtra("coins", coins);
            startActivity(gamePlayIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Завершаем текущую активность
        }
    }

    class LevelStart extends Thread {
        public void run() {
            loadLevel();
        }
    }
}