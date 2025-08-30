package com.example.airballoon;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.levels.MenuLevel;
import com.example.airballoon.managers.MediaPlayerSingleton;
import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;

import java.util.ArrayList;
import java.util.List;

public class SelectLevelActivity extends AppCompatActivity {
    private View view;
    private ImageButton buttonResumeMenu;
    private List<MenuLevel> levels;
    private final int countLevels = 16;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Основная активити, убрал для теста
        setContentView(R.layout.select_level_activity);
        view = getWindow().getDecorView();

        buttonResumeMenu = addButtonResumeMenu(this);
        useButtonResumeMenu(this);

//        buttonLevel1Image = addButtonLevel1(this);
//        useButtonLevel1(this);
        user = SaveManager.readFromFile(this);
        drawCoins();
        drawStars();


        //Если нужно будет добавить анимацю перехода
//        overridePendingTransition(R.anim.slide_in_center, R.anim.slide_out_center);
        loadLevels();
    }

    private ImageButton addButtonResumeMenu(Activity activity) {
        return buttonResumeMenu = activity.findViewById(R.id.button_resume_menu);
    }

    private void useButtonResumeMenu(Activity activity) {
        buttonResumeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Возвращаемся в меню.
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);

                //Убираем анимацию перехода.
//                overridePendingTransition(0, 0);

                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                // Завершить текущую активность
                finish();
            }
        });
    } //Используем кнопку возврата в меню

    private void loadLevels() {
        levels = new ArrayList<>();

        for(int i = 1; i <= countLevels; i++) {
            int countStars = user.getMaxLevelStars(i);

            if(i == 1) {
                levels.add(new MenuLevel(this, view, i, true, countStars));
            } else {
                boolean levelAv = user.getMaxLevelStars(i - 1) > 0;
                levels.add(new MenuLevel(this, view, i, levelAv, countStars));
            }

        }
    } //Загружаем кнопки уровней

    private void drawCoins() {
        TextView coinCountView = view.findViewById(R.id.coin_count);
        coinCountView.setText(String.valueOf(user.getCoins()));
    } //Отображаем количество монет

    private void drawStars() {
        TextView coinCountView = view.findViewById(R.id.stars_count);
        coinCountView.setText(String.valueOf(user.getStars()));
    } //Отображаем количество монет
}