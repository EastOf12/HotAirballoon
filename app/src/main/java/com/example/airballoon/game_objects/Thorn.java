package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.example.airballoon.R;
import com.example.airballoon.managers.GamePlayManager;

import java.util.Objects;
import java.util.Random;

public class Thorn {
    private final Rect rect;
    protected Activity activity;
    protected Bitmap thornImage;
    protected double percentage; // Размер изображения относительно экрана
    double width;
    double height;
    AirBalloonObject airBalloon;
    private DisplayMetrics displayMetrics;
    private int xPosition;
    private int yPosition;
    private boolean needDraw;

    public Thorn(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.airBalloon = airBalloon;
        needDraw = true;
        calculatePercentage();
        loadThornImage();
        calculateSize();
        calculateStartPosition();
        rect = new Rect(xPosition, yPosition, (int) (xPosition + width)
                , (int) (yPosition + height));
    }

    public Thorn(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon, PosX posX, int yPosition) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.airBalloon = airBalloon;
        needDraw = true;
        calculatePercentage();
        loadThornImage();
        calculateSize();
        calculateStartPosition(posX, yPosition);
        rect = new Rect(xPosition, yPosition, (int) (xPosition + width)
                , (int) (yPosition + height));
    }

    private void calculateSize() {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) thornImage.getWidth() / thornImage.getHeight();
        height = width / proportion;
        thornImage = Bitmap.createScaledBitmap(thornImage
                , (int) width, (int) height, true);
    }

    protected void loadThornImage() {
        thornImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.thorn);
    }

    protected void calculatePercentage() {
        percentage = 0.2;
    } //Определяем насколько большим по отношению к экрана должен быть шип

    public void calculateStartPosition() {
        needDraw = true;
        Random random = new Random();
        xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        yPosition = -50;
    }

    public void calculateStartPosition(PosX posX, int yPosition) {
        switch (posX) {
            case LEFT:
                xPosition = 0;
                break;
            case LEFT2:
                xPosition = (int) (width);
                break;
            case CENTER:
                xPosition = (int) (width * 2);
                break;
            case RIGHT1:
                xPosition = (int) (displayMetrics.widthPixels - (width * 2));
                break;
            case RIGHT2:
                xPosition = (int) (displayMetrics.widthPixels - width);
                break;
        }

        this.yPosition = yPosition;
    }

    private void calculateNewPosition(Canvas canvas) {
        yPosition += GamePlayManager.speed;

        if (yPosition >= canvas.getHeight() || checkCollision()) {
            yPosition = -1500;
            Random random = new Random();
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        }

        rect.left = xPosition;
        rect.top = yPosition;
        rect.right = (int) (xPosition + width);
        rect.bottom = (int) (yPosition + height);

    }

    public void drawThorn(Canvas canvas) {
        if (needDraw) {
            calculateNewPosition(canvas);

            canvas.drawBitmap(thornImage, xPosition, yPosition, null);
        }
    } //Рисуем шип

    public boolean checkCollision() {
        boolean resultCenter = airBalloon.getRects().get(0).intersect(rect);
        boolean resultUp = airBalloon.getRects().get(1).intersect(rect);
        boolean resultBottom = airBalloon.getRects().get(2).intersect(rect);
        boolean res = false;

        if (resultCenter || resultUp || resultBottom) {
            airBalloon.removeHp();
            res = true;
        }

        return res;
    }


//    public boolean checkCollision() {
//        boolean result = airBalloon.getRect().intersect(rect);
//
//        if (result) {
//            airBalloon.removeHp();
//        }
//
//        return result;
//    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thorn thorn = (Thorn) o;
        return xPosition == thorn.xPosition && yPosition == thorn.yPosition && Double.compare(thorn.percentage, percentage) == 0 && Double.compare(thorn.width, width) == 0 && Double.compare(thorn.height, height) == 0 && needDraw == thorn.needDraw && Objects.equals(activity, thorn.activity) && Objects.equals(displayMetrics, thorn.displayMetrics) && Objects.equals(thornImage, thorn.thornImage) && Objects.equals(rect, thorn.rect) && Objects.equals(airBalloon, thorn.airBalloon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, displayMetrics, thornImage, rect, xPosition, yPosition, percentage, width, height, airBalloon, needDraw);
    }

    public int getYPosition() {
        return yPosition;
    }

    public void setYPosition(int yPosition) {
        this.yPosition = yPosition;
    }
}
