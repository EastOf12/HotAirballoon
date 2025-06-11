package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.models.AirBalloon;

import java.time.LocalDateTime;

public class AirBalloonObject extends GameObject{
    float startX, startY;
    float offsetX, offsetY;
    private int collectedCoins = 0;
    private int hp = 1;
    private final int maxXp = 1;
    private boolean hadShield = false;
    private boolean hadMagnet = false;
    float newX;
    private LocalDateTime nowTimeShield;
    private LocalDateTime shieldEndTime;
    private LocalDateTime nowTimeMagnet;
    private LocalDateTime magnetEndTime;


    private int timeActionShield = 5; //Время действия щита.
    private int timeActionMagnet = 5; //Время действия щита.
    ShieldIcon shieldIcon;
    MagnetIcon magnetIcon;
    ShieldIcon shieldAirballoonAnimation;


    public AirBalloonObject(Activity activity, DisplayMetrics displayMetrics, Bitmap image) {
        super(activity, displayMetrics);

        setPercentage(0.15);
        this.image = image;
        calculateSize();
        calculateStartPosition();
        createRect();

        shieldIcon = new ShieldIcon(activity, displayMetrics);
        shieldAirballoonAnimation = new ShieldIcon(activity, displayMetrics);
        shieldAirballoonAnimation.setPercentage(0.05); //Устанавливаем размер щитов, которые будут вокруг
        shieldAirballoonAnimation.calculateSize();

        magnetIcon = new MagnetIcon(activity, displayMetrics);
    }

    @Override
    protected void calculateStartPosition() {
        xPosition = (int) (displayMetrics.widthPixels * 0.4);
        yPosition = (int) (displayMetrics.heightPixels * 0.7);
    }

    @Override
    public void draw(Canvas canvas) {
        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);
        canvas.drawBitmap(image, xPosition, yPosition, null);
        shieldTimeCounter(canvas);
        magnetTimeCounter(canvas);
    }

    public boolean onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Запоминаем начальную позицию пальца
                startX = event.getRawX();
                startY = event.getRawY();
                offsetX = xPosition - startX;
                offsetY = yPosition - startY;
                break;
            case MotionEvent.ACTION_MOVE:
                // Рассчитываем новую позицию шарика в соответствии с перемещением пальца
                newX = event.getRawX() + offsetX;

                // Обновляем позицию шарика с учетом ограничений экрана
                if (newX >= 0 && newX <= (displayMetrics.widthPixels - width)) {
                    xPosition = (int) newX;
                }
                break;
        }

        return true;
    }

    public Rect getRect() {
        return rect;
    }

    public void addCollectedCoins() {
        collectedCoins++;
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }
    public void resetCoins() {
        collectedCoins = 0;
    }

    public void removeHp() {
        if(!hadShield) {
            hp--;
        }
    }

    public int getHp() {
        return hp;
    }

    public void restartAirBalloon() {
        hp = maxXp;
        calculateStartPosition();
    }

    public void addShield() {
        hadShield = true;
        nowTimeShield = LocalDateTime.now();
        shieldEndTime = nowTimeShield.plusSeconds(timeActionShield);

        activity.runOnUiThread(() -> {
            shieldIcon.startShieldTimer(timeActionShield * 1000L);
        });
    }

    public void addMagnet() {
        hadMagnet = true;
        nowTimeMagnet = LocalDateTime.now();
        magnetEndTime = nowTimeMagnet.plusSeconds(timeActionMagnet);

        activity.runOnUiThread(() -> {
            magnetIcon.startMagnetTimer(timeActionMagnet * 1000L);
        });
    }

    public void removeShield() {
        hadShield = false;
    }

    public void removeMagnet() {
        hadMagnet = false;
    }

    public void shieldTimeCounter(Canvas canvas) {
        nowTimeShield = LocalDateTime.now();

        if(hadShield && nowTimeShield.isAfter(shieldEndTime)) {
            removeShield();
        } else if (hadShield) {
            shieldIcon.draw(canvas);
            shieldAirballoonAnimation.drawShieldAnimationAirballoon(canvas, image, xPosition, yPosition);
        }
    } //Обновляем время действия щита, удаляем щит если нужно.

    public void magnetTimeCounter(Canvas canvas) {
        nowTimeMagnet = LocalDateTime.now();

        if(hadMagnet && nowTimeMagnet.isAfter(magnetEndTime)) {
            removeMagnet();
        } else if (hadMagnet) {
            magnetIcon.draw(canvas);
        }
    } //Обновляем время действия магнита, удаляем если нужно.
}
