package com.example.airballoon.GameObjects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public abstract class GameObject {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap image;
    Rect rect;
    int xPosition;
    int yPosition;
    double width;
    double height;
    double percentage; // Размер изображения относительно экрана
    public GameObject(Activity activity, DisplayMetrics displayMetrics) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
    }

    //Рассчет размеров с сохранением пропорции изображения
    protected void calculateSize() {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) image.getWidth() / image.getHeight();
        height = width / proportion;
        image = Bitmap.createScaledBitmap(image
                , (int) width, (int) height, true);
    }

    //Рассчет начальной позиции.
    protected abstract void calculateStartPosition();

    //Отрисовка объекта
    protected abstract void draw(Canvas canvas);

    protected void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    protected void createRect() {
        rect = new Rect(xPosition, yPosition, (int) (xPosition + width)
                , (int) (yPosition + height));
    }

}
