package com.example.airballoon.managers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airballoon.GamePlayActivity;
import com.example.airballoon.R;
import com.example.airballoon.models.User;

import java.time.Instant;

public class MenuManager {
    Activity activity;
    private ImageButton buttonSetting;
    private final View view;
    private final User user;

    //Тут хранится количество уровней
    private final byte COUNT_AIRBALLOON;
    private int selectAirballoon = 1;
    private final ImageButton buttonStart;


    @SuppressLint("ClickableViewAccessibility")
    public MenuManager(Activity activity) {
        COUNT_AIRBALLOON = 3;
        this.activity = activity;

        user = SaveManager.readFromFile(activity);
        view = activity.getWindow().getDecorView();
        buttonStart = addButtonStart(view);

        useButtonStart(activity);
        drawDesiredAirballoon();
        drawCoins();
        drawSettingButton();
        useSettingButton();

        selectAirballoon();
    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonPlay() {
        return view.findViewById(R.id.button_start);
    }

    private void drawCoins() {
        TextView coinCountView = view.findViewById(R.id.coin_count);
        coinCountView.setText(String.valueOf(user.getCoins()));
    }

    //Переходим к первому уровню сразу при нажатии кнопки играть
    private void useButtonStart(Activity activity) {
        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
        });
    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonStart(View view) {
        return view.findViewById(R.id.button_start);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void selectAirballoon() {

        HorizontalScrollView scrollView = view.findViewById(R.id.scrollable);
        RelativeLayout[] relativeLayouts = new RelativeLayout[COUNT_AIRBALLOON];

        relativeLayouts[0] = view.findViewById(R.id.linear_layout1);
        relativeLayouts[1] = view.findViewById(R.id.linear_layout2);
        relativeLayouts[2] = view.findViewById(R.id.linear_layout3);

        //Нужны для работы логики быстрого скролла
        final long[] timeStartTouch = new long[1];
        final long[] timeFinishTouch = new long[1];
        final ScrollDirection[] scrollDirection = new ScrollDirection[1];

        final int[] startTouchPosition = new int[1];
        final int[] finishTouchPosition = new int[1];


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("CheckResult")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:

                        //Получаем начальную позицию экрана шарика по x.
                        float linearLayout1StartW = relativeLayouts[0].getLeft();
                        float linearLayout2StartW = relativeLayouts[1].getLeft();
                        float linearLayout3StartW = relativeLayouts[2].getLeft();

                        //Запоминаем, что пока перемещать не надо
                        scrollDirection[0] = ScrollDirection.NOT_DIRECTION;
                        int maxArea = 0;
                        int maxIndex = -1;
                        int area;


                        //Получаем индекс шарика, который занимает наибольшее количество пространства
                        for (int i = 0; i < relativeLayouts.length; i++) {
                            RelativeLayout layout = relativeLayouts[i];

                            Rect rect = new Rect();
                            layout.getGlobalVisibleRect(rect);
                            Rect visibleRect = new Rect();
                            scrollView.getGlobalVisibleRect(visibleRect);

                            Rect intersection = new Rect();
                            intersection.setIntersect(rect, visibleRect);

                            // Вычислить площадь пересечения
                            area = intersection.width() * intersection.height();
                            if (area > maxArea) {
                                System.out.println(area);
                                maxArea = area;
                                maxIndex = i;
                            }
                        }

                        //Проверяем на быстрый скролл и при необходимости корректируем индекс нужного шарика.
                        timeFinishTouch[0] = Instant.now().toEpochMilli();
                        finishTouchPosition[0] = (int) event.getRawX();

                        if(timeFinishTouch[0] - timeStartTouch[0] <= 150) {
                            if(startTouchPosition[0] - finishTouchPosition[0] > 100) {
                                scrollDirection[0] = ScrollDirection.RIGHT;
                            } else if (startTouchPosition[0] - finishTouchPosition[0] < -100) {
                                scrollDirection[0] = ScrollDirection.LEFT;
                            }
                        }

                        if(scrollDirection[0] == ScrollDirection.RIGHT && maxIndex != relativeLayouts.length - 1) {
                            maxIndex++;
                        } else if (scrollDirection[0] == ScrollDirection.LEFT && maxIndex != 0) {
                            maxIndex--;
                        }

                        //Перемещаем на нужный шарик при необходимости.
                        selectAirballoon = maxIndex + 1;
                        user.setSelectAirBalloon(selectAirballoon);

                        //Сохраняем в файле выбранный шарик
                        SaveManager.save(activity, user);
                        System.out.println("Выбрали шарик с id " + selectAirballoon);

                        scrollView.post(new Runnable() {
                            public void run() {
                                if(user.getSelectAirBalloon() == 1){
                                    scrollView.smoothScrollTo((int) linearLayout1StartW, 0);
                                } else if (user.getSelectAirBalloon() == 2) {
                                    scrollView.smoothScrollTo((int) linearLayout2StartW, 0);
                                } else {
                                    scrollView.smoothScrollTo((int) linearLayout3StartW, 0);
                                }
                            }});
                        break;
                    case MotionEvent.ACTION_DOWN:
                        //Получаем время нажатия на шарик
                        timeStartTouch[0] = Instant.now().toEpochMilli();
                        startTouchPosition[0] = (int) event.getRawX();
                        break;
                }
                return false;
            }
        });
    }

    private void drawDesiredAirballoon() {
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.scrollable);
        RelativeLayout relativeLayout = null;
        switch (user.getSelectAirBalloon()) {
            case 1:
                relativeLayout = (RelativeLayout) horizontalScrollView.findViewById(R.id.linear_layout1);
                break;
            case 2:
                relativeLayout = (RelativeLayout) horizontalScrollView.findViewById(R.id.linear_layout2);
                break;
            case 3:
                relativeLayout = (RelativeLayout) horizontalScrollView.findViewById(R.id.linear_layout3);
                break;
        }

        relativeLayout.setVisibility(View.VISIBLE);
        RelativeLayout finalRelativeLayout = relativeLayout;
        horizontalScrollView.post(() -> horizontalScrollView.scrollBy((int) finalRelativeLayout.getLeft(), 0));

    }

    private enum ScrollDirection{
        LEFT,
        RIGHT,
        NOT_DIRECTION
    }

    private void drawSettingButton() {
        buttonSetting = activity.findViewById(R.id.button_setting);

    }

    private void useSettingButton() {
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Нажали на кнопку настроек.");
            }
        });
    }

}


