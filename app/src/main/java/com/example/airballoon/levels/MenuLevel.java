package com.example.airballoon.levels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airballoon.GamePlayActivity;
import com.example.airballoon.R;
import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;

//Описывает уровень в игре
public class MenuLevel {
    private View view;
    private Activity activity;
    private ImageButton imageButton;
    private Bitmap doorLockBitmap;
    private int levelNumber;
    private int stars;

    public MenuLevel(
            Activity activity,
            View view,
            int levelNumber,
            boolean levelAvailable,
            int stars) {
        this.activity = activity;
        this.view = view;
        this.levelNumber = levelNumber;
        this.stars = stars;

        loadImageButton();

        //В зависимости от доступности уровня отображаем нужный эффект
        if(levelAvailable) {
            useButton();
            addLevelNumber();
            addStars();
        } else {
            addDoorLock();
        }
    }

    private void addDoorLock() {
        RelativeLayout relativeLayout = view.findViewById(getLayoutId());
        doorLockBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.door_lock);

        if (relativeLayout != null) {
            ImageView doorLockImageView = new ImageView(activity);
            doorLockImageView.setImageBitmap(doorLockBitmap);

            int lockWidth = 100; //Ширина
            int lockHeight = 100; //Высота

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lockWidth, lockHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            doorLockImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            relativeLayout.addView(doorLockImageView, params);
        }
    } //Добавляем на кнопку замок



    private void useButton() {
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Отобразить ProgressBar
                ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);

                // Запустить игру
                Intent intent = new Intent(activity, GamePlayActivity.class);
                intent.putExtra("levelNumber", levelNumber);
                activity.startActivity(intent);
                //Убираем анимацию перехода.
//                    activity.overridePendingTransition(0, 0);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                // Завершить текущую активность
                activity.finish();
            }
        });
    } //Запускает уровень, если было нажатие на кнопку

    private void addLevelNumber() {
        RelativeLayout relativeLayout = view.findViewById(getLayoutId());

        if (relativeLayout != null) {
            //Добавляем номер уровня
            TextView textView = new TextView(activity);
            textView.setText(String.valueOf(levelNumber));
            textView.setTextSize(24); // Размер текста
            textView.setGravity(Gravity.CENTER); //Расположение относительно слоя
            textView.setTextColor(Color.WHITE); //Цвет текста

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.topMargin = 30;
            relativeLayout.addView(textView, params);
        }
    } //Добавляем номер уровня на кнопку

    private void addStars() {
        RelativeLayout relativeLayout = view.findViewById(getLayoutId());

        if (relativeLayout != null) {
            Bitmap yellowStarBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.star_yellow);
            Bitmap brawnStarBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.star_brown);

            // Ширина и высота звездочек
            int starWidth = 65;
            int starHeight = 65;

            // Смещение по оси X для первой звезды
            int firstStarMarginLeft = 19;

            int yellowStars = stars;

            for (int i = 0; i < 3; i++) {
                ImageView starImageView = new ImageView(activity);

                //Выбираем какие звезды рисовать
                if(yellowStars != 0) {
                    starImageView.setImageBitmap(yellowStarBitmap);
                    yellowStars--;
                } else {
                    starImageView.setImageBitmap(brawnStarBitmap);
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(starWidth, starHeight);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // Устанавливаем нижнее положение

                if (i == 0) {
                    params.leftMargin = firstStarMarginLeft; // Устанавливаем отступ для первой звезды
                } else {
                    params.leftMargin = firstStarMarginLeft + i * (starWidth - 30); //Отступ между звездами
                }

                starImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                relativeLayout.addView(starImageView, params);
            }
        }
    }

    private void loadImageButton() {
        String levelIdString = "level_" + levelNumber + "_image";

        @SuppressLint("DiscouragedApi")
        int levelId = activity.getResources().getIdentifier(levelIdString, "id", activity.getPackageName());
        imageButton = activity.findViewById(levelId);
    } //Загружаем фон кнопки

    @SuppressLint("DiscouragedApi")
    private int getLayoutId() {
        String levelIdString = "level_" + levelNumber;

        return activity.getResources().getIdentifier(levelIdString, "id", activity.getPackageName());
    }
}