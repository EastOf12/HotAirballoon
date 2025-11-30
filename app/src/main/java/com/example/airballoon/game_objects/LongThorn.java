package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.example.airballoon.R;

public class LongThorn extends Thorn {
    public LongThorn(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        super(activity, displayMetrics, airBalloon);
    }

    @Override
    protected void loadThornImage() {
        thornImage = BitmapFactory.decodeResource(activity.getResources(), R.drawable.long_thorn);
    }

    @Override
    protected void calculatePercentage() {
        percentage = 0.4;
    }
}
