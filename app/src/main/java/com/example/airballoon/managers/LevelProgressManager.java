package com.example.airballoon.managers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.StateSet;

import com.example.airballoon.R;
import com.example.airballoon.game_objects.BaseObject;
import com.example.airballoon.levels.FreeLevel;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelProgressManager {
    ProgressBarBg progressBarBg;
    ProgressBar progressBar;
    ProgressSign progressSign;
    ArrayList<StarProgress> stars = new ArrayList<>();


    public LevelProgressManager(Activity activity, DisplayMetrics displayMetrics, int levelNum) {
        progressBarBg = new ProgressBarBg(activity, displayMetrics, R.drawable.level_progress_bg, 0.045);
        progressBar = new ProgressBar(activity, displayMetrics, R.drawable.level_progress, 0.023, progressBarBg, levelNum);
        progressSign = new ProgressSign(activity, displayMetrics, 0.05, progressBarBg);

        for(int i = 1; i < 4; i++) {
            StarProgress starProgress = new StarProgress(activity, displayMetrics, R.drawable.star_brown, 0.08, progressBarBg, i, levelNum);
            stars.add(starProgress);
        }
    }

    public void run(Canvas canvas, int distance) {
        progressBarBg.draw(canvas);
        progressBar.draw(canvas, distance);
        progressSign.draw(canvas, distance);

        for (StarProgress star: stars) {
            star.draw(canvas, distance);
        }
    }

}

class ProgressSign extends BaseObject {
    ProgressBarBg progressBar;
    Paint textPaint;

    public ProgressSign(Activity activity, DisplayMetrics displayMetrics,
                        double percentage, ProgressBarBg progressBar) {
        super(activity, displayMetrics, percentage);
        this.progressBar = progressBar;

        setPositions((int) (progressBar.getXPos() + progressBar.getWidth() * 0.5),
                (int) (progressBar.getYPos() + progressBar.getHeight() * 1.1));

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }


    public void draw(Canvas canvas, int distance) {

        canvas.drawText(String.valueOf(distance / 100)
                , xPosition
                , yPosition, textPaint);
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
    HashMap<Integer, Integer> levelsInfo = FreeLevel.loadLevelFinishInfo();
    double minYPos;
    int extremePoint; //Крайняя точка по Y внизу
    int maxHeight;

    int maxDistance;
    public ProgressBar(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                       double percentage, ProgressBarBg progressBarBg, int levelNum) {
        super(activity, displayMetrics, percentage);
        maxDistance = levelsInfo.get(levelNum) * 100;
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
        double newHeight = interpolateHeight(distance);

        if (newHeight > 1) {
            height = (int) newHeight;
            yPosition = (int) (extremePoint - height);
            image = Bitmap.createScaledBitmap(image, (int) width, (int) height, true);
        }

        canvas.drawBitmap(image, xPosition, yPosition, null);
    }

    private double interpolateHeight(int distance) {
        double percent = (double) distance / maxDistance;

        if (percent <= 0.5) {
            return maxHeight * (percent / 0.5 * 0.35);
        } else if (percent <= 0.75) {
            return maxHeight * (0.35 + (percent - 0.5) / (0.75 - 0.5) * (0.60 - 0.35));
        } else {
            return maxHeight * (0.60 + (percent - 0.75) / (1.00 - 0.75) * (1.00 - 0.60));
        }
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

class StarProgress extends BaseObject {
    HashMap<Integer, Integer> levelsInfo = FreeLevel.loadLevelFinishInfo();
    boolean yellowStar = false;
    Bitmap yellowStarImage;
    int yellowStarDistance;
    int starNumber;
    DisplayMetrics displayMetrics;

    public StarProgress(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                       double percentage, ProgressBarBg progressBarBg, int starNumber, int LevelNum) {
        super(activity, displayMetrics, percentage);
        this.starNumber = starNumber;
        this.displayMetrics = displayMetrics;
        setYellowStarDistance(LevelNum);

        yellowStarImage= BitmapFactory.decodeResource(activity.getResources(), R.drawable.star_yellow);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        int yPos;
        int xPos = progressBarBg.getXPos() - (int) (width * 0.21);;
        int bgYPos = progressBarBg.getYPos();


        switch (starNumber) {
            case 1:
                yPos = bgYPos + (int) (progressBarBg.getHeight() * 0.55);
                break;
            case 2:
                yPos = bgYPos + (int) (progressBarBg.getHeight() * 0.2);
                break;
            default:
                yPos = bgYPos - (int) (height * 0.5);
                break;
        }

        setPositions(xPos, yPos);
    }

    public void setImage(int distance) {
        if(starNumber == 1 && !yellowStar && distance > (yellowStarDistance * 100)) {
            image = yellowStarImage;
            calculateSize(displayMetrics);
        } else if(starNumber == 2 && !yellowStar && distance > (yellowStarDistance * 100)) {
            image = yellowStarImage;
            calculateSize(displayMetrics);
        } else if(starNumber == 3 && !yellowStar && distance >= (yellowStarDistance * 100)) {
            image = yellowStarImage;
            calculateSize(displayMetrics);
        }
    }

    public void draw(Canvas canvas, int distance) {
        setImage(distance);

        canvas.drawBitmap(image, xPosition, yPosition,
                null);
    }

    private void setYellowStarDistance(int levelNum) {
        switch (starNumber) {
            case 1:
                yellowStarDistance = (int) (levelsInfo.get(levelNum) * 0.4);
                break;
            case 2:
                yellowStarDistance = (int) (levelsInfo.get(levelNum) * 0.79);
                break;
            case 3:
                yellowStarDistance = levelsInfo.get(levelNum);
                break;
        }
    }
}

