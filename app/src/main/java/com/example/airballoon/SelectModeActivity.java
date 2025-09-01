package com.example.airballoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;

public class SelectModeActivity extends AppCompatActivity {
    private View view;
    private ImageButton modeFree;
    private ImageButton buttonResumeMenu;
    private ImageButton buttonModeOne;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Основная активити, убрал для теста
        setContentView(R.layout.select_mode_activity);
        view = getWindow().getDecorView();

        buttonResumeMenu = addButtonResumeMenu(this);
        useButtonResumeMenu(this);

        buttonModeOne = addButtonModeOne(this);
        useButtonModeOne(this);

        modeFree = addButtonFreeMode(this);
        useButtonFreeMode(this);

        user = SaveManager.readFromFile(this);
        drawCoins();
        drawStars();
    }

    private ImageButton addButtonFreeMode(Activity activity) {
        return modeFree = activity.findViewById(R.id.button_mode_free);
    }

    private void useButtonFreeMode(Activity activity) {
        modeFree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                //Переходим к экрану выбора уровня в первом режиме
                Intent intent = new Intent(activity, SelectLevelActivity.class);
                activity.startActivity(intent);


                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                // Завершить текущую активность
                activity.finish();
            }
        });
    } //Переходим к испытанию

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

    private ImageButton addButtonModeOne(Activity activity) {
        return buttonModeOne = activity.findViewById(R.id.button_mode_one);
    }

    private void useButtonModeOne(Activity activity) {
        buttonModeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Возвращаемся в меню.
                Intent intent = new Intent(activity, SelectLevelActivity.class);
                startActivity(intent);

                //Убираем анимацию перехода.
//                overridePendingTransition(0, 0);

                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                // Завершить текущую активность
                finish();
            }
        });
    } //Используем кнопку возврата в меню

    private void drawCoins() {
        TextView coinCountView = view.findViewById(R.id.coin_count);
        coinCountView.setText(String.valueOf(user.getCoins()));
    } //Отображаем количество монет

    private void drawStars() {
        TextView coinCountView = view.findViewById(R.id.stars_count);
        coinCountView.setText(String.valueOf(user.getStars()));
    } //Отображаем количество монет
}
