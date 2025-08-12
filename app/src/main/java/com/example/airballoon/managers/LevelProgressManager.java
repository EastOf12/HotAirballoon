package com.example.airballoon.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.StateSet;

import com.example.airballoon.R;
import com.example.airballoon.game_objects.BaseObject;

import java.util.ArrayList;

public class LevelProgressManager {
    ProgressBarBg progressBarBg;
    ProgressBar progressBar;
    ArrayList<StarProgress> stars = new ArrayList<>();

    public LevelProgressManager(Activity activity, DisplayMetrics displayMetrics) {
        progressBarBg = new ProgressBarBg(activity, displayMetrics, R.drawable.level_progress_bg, 0.045);
        progressBar = new ProgressBar(activity, displayMetrics, R.drawable.level_progress, 0.023, progressBarBg);

        for(int i = 1; i < 4; i++) {
            StarProgress starProgress = new StarProgress(activity, displayMetrics, R.drawable.star_brown, 0.3, progressBarBg, i);
            stars.add(starProgress);
        }
    }

    public void run(Canvas canvas, int distance) {
        progressBarBg.draw(canvas);
        progressBar.draw(canvas, distance);

//        for (StarProgress star: stars) {
//            star.draw(canvas);
//        }
    }

}

class ProgressBarBg extends BaseObject {
    public ProgressBarBg(Activity activity, DisplayMetrics displayMetrics, int resourceId, double percentage) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions((int) (displayMetrics.widthPixels * 0.9),
                (int) (displayMetrics.heightPixels * 0.25));
    }

    public int getYPos() {
        return yPosition;
    }

    public int getXPos() {
        return xPosition;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}

class ProgressBar extends BaseObject {
    double minYPos;
    int extremePoint; //Крайняя точка по Y внизу
    int maxHeight;

    int maxDistance = 500_00;
    public ProgressBar(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                       double percentage, ProgressBarBg progressBarBg) {
        super(activity, displayMetrics, percentage);
        minYPos = (int) (progressBarBg.getYPos() + progressBarBg.getHeight() * 0.050);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);
        height = 5;

        image = Bitmap.createScaledBitmap(image
                , (int) width, (int) height, true);

        int bgYPos = progressBarBg.getYPos();
        int bgXPos = progressBarBg.getXPos();

        int yPos = bgYPos + (int) (progressBarBg.getHeight() * 0.943);
        int xPos = (int) (bgXPos + (progressBarBg.getWidth() * 0.5) - (width * 0.445));

        extremePoint = (int) (yPos + height);

        maxHeight = (int) (extremePoint - minYPos);

        setPositions(xPos, yPos);
    }

    public void draw(Canvas canvas, int distance) {
        double percent = (double) distance / maxDistance;

        if(yPosition > minYPos && percent > 0) {
            height = maxHeight * percent;

            if(height > 1) {
                yPosition = (int) (extremePoint - height);
                image = Bitmap.createScaledBitmap(image
                        , (int) width, (int) height, true);
            }
        }

        canvas.drawBitmap(image, xPosition, yPosition,
                null);

    }
}

class StarProgress extends BaseObject {
    public StarProgress(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                       double percentage, ProgressBarBg progressBarBg, int starNumber) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        int yPos;
        int xPos;
        int bgYPos = progressBarBg.getYPos();
        int bgXPos = progressBarBg.getXPos();

        switch (starNumber) {
            case 1:
                yPos = bgYPos - (int) (width / 2);
                xPos = bgXPos + (int) (width * 0.2);
                break;
            case 2:
                yPos = bgYPos - (int) (height * 0.85);
                xPos = (int) (bgXPos + (progressBarBg.getWidth() * 0.5) - (width * 0.5));
                break;
            default:
                yPos = bgYPos - (int) (width / 2);
                xPos = bgXPos + (int) (progressBarBg.getWidth() * 0.7);
                break;
        }

        setPositions(xPos, yPos);
    }
}

