package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;

public class BackGround {
    Bitmap backGroundImage;

    public BackGround(Activity activity, DisplayMetrics displayMetrics, int idImage) {
        backGroundImage = BitmapFactory.decodeResource(activity.getResources(), idImage);
        backGroundImage = Bitmap.createScaledBitmap(backGroundImage, displayMetrics.widthPixels
                , (int) (displayMetrics.heightPixels * 1.1), true);
    }

    public void drawBackgroundImage(Canvas canvas) {
        canvas.drawBitmap(backGroundImage, 0, 0, null);
    } //Рисуем фон из картинки

    public void drawBackgroundFillColor(Canvas canvas) {
        int fillColor = Color.parseColor("#87CEFA");
        canvas.drawColor(fillColor);
    } //Рисуем фон залитый сплошным текстом.
}
