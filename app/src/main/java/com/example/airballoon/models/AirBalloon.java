package com.example.airballoon.models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

public class AirBalloon {

    private boolean availability = false;
    public AirBalloon() {
        this.availability = false;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"
                + "availability=" + availability +
                '}';
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

}
