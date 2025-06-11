package com.example.airballoon.managers;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.airballoon.GamePlayActivity;
import com.example.airballoon.R;
import com.example.airballoon.SelectLevelActivity;
import com.example.airballoon.models.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

public class MenuManager {
    Activity activity;
    private ImageButton buttonSetting;
    private final ImageButton buttonStart;
    private final ImageButton buttonBuy;
    private final View view;
    private final User user;
    private final byte COUNT_AIRBALLOON;
    private int selectAirballoon = 1;
    private final TextView priceAirballoonView;
    private final int priceAirballoon1 = 5000;
    private final int priceAirballoon2 = 15000;
    private final ImageView longCloud;
    private final Bitmap cloudBitmap;
    private final ImageView groupClouds;
    private final Bitmap groupCloudsBitmap;
    private final WindowManager windowManager;
    private final Random random;
    private boolean running = true;

    private ImageButton selectLevel;

    MediaPlayer player;


    @SuppressLint("ClickableViewAccessibility")
    public MenuManager(Activity activity, WindowManager windowManager) {
        COUNT_AIRBALLOON = 3;
        this.activity = activity;
        this.windowManager = windowManager;

        random = new Random();
        user = SaveManager.readFromFile(activity);
        view = activity.getWindow().getDecorView();
        buttonStart = addButtonStart(view);
        buttonBuy = addButtonBuy(view);
        priceAirballoonView = view.findViewById(R.id.price_airballoon);
        selectLevel = addButtonPlay();

        longCloud = view.findViewById(R.id.cloud_long);
        cloudBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.cloud_long);
        longCloud.setImageBitmap(Bitmap.createScaledBitmap(cloudBitmap, 200, 100, true));

        groupClouds = view.findViewById(R.id.group_clouds);
        groupCloudsBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.group_clouds);
        groupClouds.setImageBitmap(Bitmap.createScaledBitmap(groupCloudsBitmap, 400, 300, true));

        selectAirballoon = user.getSelectAirBalloon();
    }

    public void startGame() {

        //Переводим к активити выбора уровня при нажатии кнопки плей
        useButtonSelectLevel();

        //Запускаем игру при нажатии кнопки плей
//        useButtonStart(activity);


        useButtonBuy(activity);
        drawDesiredAirballoon();
        drawCoins();
        drawSettingButton();
        useSettingButton();
        selectAirballoon();
        calculateStartPositionCloud(longCloud);
        calculateStartPositionGroupClouds(groupClouds);
        runClouds(longCloud);
        changStatusButtonStartBuy();

        player = MediaPlayerSingleton.getInstance(activity);
        if (!player.isPlaying()) {
            player.start(); // Запустим, если еще не запущен
        }

    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonPlay() {
        return view.findViewById(R.id.button_start);
    }

    private void drawCoins() {
        TextView coinCountView = view.findViewById(R.id.coin_count);
        coinCountView.setText(String.valueOf(user.getCoins()));
    }

    @SuppressLint("SetTextI18n")
    private void setAirballoonPrice(int price) {
        priceAirballoonView.setText(String.valueOf(price));
    }

    private void choosePrice() {
        if(checkAvailableAirBalloon()) {
            priceAirballoonView.setVisibility(View.GONE);
        } else {

            if(selectAirballoon == 2) {
                setAirballoonPrice(priceAirballoon1);
            } else {
                setAirballoonPrice(priceAirballoon2);
            }

            priceAirballoonView.setVisibility(View.VISIBLE);
        }
    }

    private void useButtonStart(Activity activity) {

        buttonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                    stopClouds();
                    // Отобразить ProgressBar
                    ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                    progressBar.setVisibility(View.VISIBLE);

                    // Запустить игру
                    Intent intent = new Intent(activity, GamePlayActivity.class);
                    activity.startActivity(intent);

                    activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    //Убираем анимацию перехода.
//                    activity.overridePendingTransition(0, 0);

                    // Завершить текущую активность
                    activity.finish();

                player.stop();
            }
        });
    }

    private void useButtonBuy(Activity activity) {

        buttonBuy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(selectAirballoon == 2) {
                    if(user.getCoins() >= priceAirballoon1) {
                        user.takeCoins(priceAirballoon1);
                        user.addAirBalloon(selectAirballoon);
                        changStatusButtonStartBuy();
                        drawCoins(); //Обновляем оставшиеся деньги
                        SaveManager.save(activity, user);
                    }
                } else if (selectAirballoon == 3) {
                    if(user.getCoins() >= priceAirballoon2) {
                        user.takeCoins(priceAirballoon2);
                        user.addAirBalloon(selectAirballoon);
                        changStatusButtonStartBuy();
                        drawCoins(); //Обновляем оставшиеся деньги
                        SaveManager.save(activity, user);
                    }
                }
            }
        });
    } //Производим покупку, если денег пользователя достаточно и открыаем доступ к шарику.

    //Переход на экран выбор уровня
    private void useButtonSelectLevel() {
        selectLevel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Отобразить ProgressBar
//                ProgressBar progressBar = view.findViewById(R.id.progress_bar);
//                progressBar.setVisibility(View.VISIBLE);
//
////                Скрыть кнопку
//                selectLevel.setVisibility(View.INVISIBLE);

                // Запустить игру
                Intent intent = new Intent(activity, SelectLevelActivity.class);
                activity.startActivity(intent);

                //Убираем анимацию перехода.
//                activity.overridePendingTransition(0, 0);

                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                // Завершить текущую активность
                activity.finish();
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonStart(View view) {
        return view.findViewById(R.id.button_start);
    }

    @SuppressLint("WrongViewCast")
    private ImageButton addButtonBuy(View view) {
        return view.findViewById(R.id.button_buy);
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

                        //Отображаем нужную кнопку в зависимости от доступности шарика
                        changStatusButtonStartBuy();
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

    private boolean checkAvailableAirBalloon() {
        return user.getAvailableBalls().contains(selectAirballoon);
    } //Проверяем доступен ли шарик пользователю

    private void changStatusButtonStartBuy() {
        if(checkAvailableAirBalloon()) {
            buttonStart.setVisibility(View.VISIBLE);
            buttonBuy.setVisibility(View.GONE);
        } else {
            buttonStart.setVisibility(View.GONE);
            buttonBuy.setVisibility(View.VISIBLE);
        }

        choosePrice();
    }

    public void shiftLongCloudLeft(ImageView longCloud, float shiftAmount) {
        // Получаем текущую матрицу изображения
        Matrix currentMatrix = longCloud.getImageMatrix();
        float[] values = new float[9];
        currentMatrix.getValues(values);

        // Изменяем значения матрицы для сдвига
        float currentTranslateX = values[Matrix.MTRANS_X]; // Получаем текущее смещение по X
        float currentTranslateY = values[Matrix.MTRANS_Y];
        if(currentTranslateX > windowManager.getDefaultDisplay().getWidth()) {
            currentTranslateX = random.nextInt(-300 - (-1000) + 1) + (-1000);
            currentTranslateY = random.nextInt(700);
        }
        float newTranslateX = currentTranslateX - shiftAmount; // Вычисляем новое смещение

        // Создаем новую матрицу и устанавливаем новое смещение
        Matrix newMatrix = new Matrix();
        newMatrix.set(currentMatrix); // Копируем текущую матрицу
        newMatrix.setTranslate(newTranslateX, currentTranslateY); // Обновляем только смещение по X

        // Устанавливаем новую матрицу на ImageView
        longCloud.setImageMatrix(newMatrix);
    }

    public void shiftGroupCloudsLeft(ImageView groupClouds, float shiftAmount) {
        // Получаем текущую матрицу изображения
        Matrix currentMatrix = groupClouds.getImageMatrix();
        float[] values = new float[9];
        currentMatrix.getValues(values);

        // Изменяем значения матрицы для сдвига
        float currentTranslateX = values[Matrix.MTRANS_X]; // Получаем текущее смещение по X
        float currentTranslateY = values[Matrix.MTRANS_Y];
        if(currentTranslateX > windowManager.getDefaultDisplay().getWidth()) {
            currentTranslateX = random.nextInt(-300 - (-1000) + 1) + (-1000);
            currentTranslateY = random.nextInt(700);
        }
        float newTranslateX = currentTranslateX - shiftAmount; // Вычисляем новое смещение

        // Создаем новую матрицу и устанавливаем новое смещение
        Matrix newMatrix = new Matrix();
        newMatrix.set(currentMatrix); // Копируем текущую матрицу
        newMatrix.setTranslate(newTranslateX, currentTranslateY); // Обновляем только смещение по X

        // Устанавливаем новую матрицу на ImageView
        groupClouds.setImageMatrix(newMatrix);
    }

    public void calculateStartPositionCloud(ImageView longCloud) {

        // Устанавливаем стартовую позицию - так, чтобы облак был вне правой части экрана
        float startX = random.nextInt(-300 - (-1000) + 1) + (-1000); // Начальная позиция за пределами экрана по X
        float startY = random.nextInt(700);

        // Создаем новую матрицу и устанавливаем стартовую позицию
        Matrix newMatrix = new Matrix();
        newMatrix.setTranslate(startX, startY); // Устанавливаем начинающую позицию

        // Устанавливаем новую матрицу на ImageView
        longCloud.setImageMatrix(newMatrix);
    }

    public void calculateStartPositionGroupClouds(ImageView groupClouds) {

        // Устанавливаем стартовую позицию - так, чтобы облак был вне правой части экрана
        float startX = random.nextInt(100); // Начальная позиция за пределами экрана по X
        float startY = random.nextInt(700);

        // Создаем новую матрицу и устанавливаем стартовую позицию
        Matrix newMatrix = new Matrix();
        newMatrix.setTranslate(startX, startY); // Устанавливаем начинающую позицию

        // Устанавливаем новую матрицу на ImageView
        groupClouds.setImageMatrix(newMatrix);
    }

    public void runClouds(ImageView longCloud) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDateTime now;
                LocalDateTime last = LocalDateTime.now();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                while (running) {
                    now = LocalDateTime.now();
                    // Проверяем, прошло ли 1/60 секунды
                    if (Duration.between(last, now).toNanos() >= 16666667) { // 1/60 секунды в наносекундах
                        shiftLongCloudLeft(longCloud, -1f);
                        shiftGroupCloudsLeft(groupClouds, -1);
                        last = now;
                    }

                    // Небольшая пауза, чтобы избежать избыточной нагрузки на процессор
                    try {
                        Thread.sleep(1); // Пауза на 1 миллисекунду
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Восстановление статуса прерывания
                    }
                }
            }
        });
        thread.start(); // Запускаем поток
    } //Запускаем генерацию облаков в отдельном потоке

    public void stopClouds() {
        running = false; // Устанавливаем флаг в false
    }

}


