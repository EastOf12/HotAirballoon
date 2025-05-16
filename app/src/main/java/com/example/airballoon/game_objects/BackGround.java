package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

public class BackGround {
    Bitmap backGroundImagePiece1;
    Bitmap backGroundImagePiece2;
    private int heightPixels;
    private final int weightPixels = 0;
    private final int displayMetricsHeight;
    private final int coefficient = 2; //Во сколько раз высота картинки должна быть больше, чем высота экрана

    public BackGround(Activity activity, DisplayMetrics displayMetrics, int idImage) {
        displayMetricsHeight = displayMetrics.heightPixels;

        backGroundImagePiece1 = BitmapFactory.decodeResource(activity.getResources(), idImage);
        backGroundImagePiece2 = BitmapFactory.decodeResource(activity.getResources(), idImage);

        backGroundImagePiece1 = Bitmap.createScaledBitmap(backGroundImagePiece1, displayMetrics.widthPixels * coefficient
                , (int) (displayMetricsHeight * coefficient), true);

        backGroundImagePiece2 = Bitmap.createScaledBitmap(backGroundImagePiece2, displayMetrics.widthPixels * coefficient
                , (int) (displayMetricsHeight * coefficient), true);


        heightPixels = -displayMetricsHeight * (coefficient - 1);
    }

    public void drawBackgroundImage(Canvas canvas, int speed) {
        heightPixels += (speed / 3);
        canvas.drawBitmap(backGroundImagePiece1, weightPixels, heightPixels, null);
        canvas.drawBitmap(backGroundImagePiece2, weightPixels, (heightPixels - displayMetricsHeight * coefficient), null);

        if (heightPixels >= displayMetricsHeight) {
            heightPixels = -displayMetricsHeight * (coefficient - 1);
        }

    } //Рисуем фон

    public void restartBackground() {
        heightPixels = -displayMetricsHeight * (coefficient - 1);
    }
}
