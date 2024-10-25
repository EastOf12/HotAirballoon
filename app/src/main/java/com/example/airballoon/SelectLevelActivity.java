package com.example.airballoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class SelectLevelActivity extends AppCompatActivity {
    private View view;
    private ImageButton buttonStart;
    private ImageButton buttonResumeMenu;
    private ImageButton buttonLevel1Image;
    boolean isLevel1 = false;

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

        View view = this.getWindow().getDecorView();
        buttonStart = addButtonStart(view);
        useButtonStart(this);

        buttonResumeMenu = addButtonResumeMenu(this);
        useButtonResumeMenu(this);

        buttonLevel1Image = addButtonLevel1(this);
        useButtonLevel1(this);




        //Если нужно будет добавить анимацю перехода
//        overridePendingTransition(R.anim.slide_in_center, R.anim.slide_out_center);

    }

    private void useButtonStart(Activity activity) {
        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("Нажали на кнопку старт");
                if(isLevel1) {
                    // Отобразить ProgressBar
                    ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);

                    // Запустить игру
                    Intent intent = new Intent(activity, GamePlayActivity.class);
                    activity.startActivity(intent);

                    //Убираем анимацию перехода.
                    activity.overridePendingTransition(0, 0);

                    // Завершить текущую активность
                    activity.finish();
                }
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonStart(View view) {
        return view.findViewById(R.id.button_start);
    }

    private ImageButton addButtonResumeMenu(Activity activity) {
        return buttonResumeMenu = activity.findViewById(R.id.button_resume_menu);
    }

    private void useButtonResumeMenu(Activity activity) {
        buttonResumeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Нажали на кнопку возврата в меню");

                //Возвращаемся в меню.
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);

                //Убираем анимацию перехода.
                overridePendingTransition(0, 0);

                // Завершить текущую активность
                finish();
            }
        });
    }

    private ImageButton addButtonLevel1(Activity activity) {
        return buttonResumeMenu = activity.findViewById(R.id.level_1_image);
    }

    private boolean selectButtonLevel1() {
        isLevel1 = !isLevel1;

        //Меняем цвет кнопки (Пока только для уровня 1, исправить при рефакторе)
        if (isLevel1) {
            buttonLevel1Image.setImageResource(R.drawable.button_level_1_selected);
        } else {
            buttonLevel1Image.setImageResource(R.drawable.button_level_1);
        }

        return false;
    }

    private void useButtonLevel1(Activity activity) {
        buttonLevel1Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Нажали на кнопку уровня 1");
                selectButtonLevel1();

//                //Возвращаемся в меню.
//                Intent intent = new Intent(activity, MainActivity.class);
//                startActivity(intent);
//
//                //Убираем анимацию перехода.
//                overridePendingTransition(0, 0);
//
//                // Завершить текущую активность
//                finish();
            }
        });
    }
}
