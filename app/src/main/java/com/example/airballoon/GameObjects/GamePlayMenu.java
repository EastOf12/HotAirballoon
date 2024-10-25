package com.example.airballoon.GameObjects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;


import com.example.airballoon.R;
import com.example.airballoon.managers.MenuActions;

public class GamePlayMenu {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap buttonResume;
    Bitmap buttonExit;
    int xPositionButtonResume;
    int yPositionButtonResume;

    int xPositionButtonExit;
    int yPositionButtonExit;
    double percentage = 0.25; // Размер изображения относительно экрана
    double widthButtonResume;
    double heightButtonResume;
    double widthButtonExit;
    double heightButtonExit;

    public GamePlayMenu(Activity activity, DisplayMetrics displayMetrics) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        buttonResume = BitmapFactory.decodeResource(activity.getResources(), R.drawable.resume);
        buttonExit = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit_game_play);
        calculateSize();
        calculateStartPosition();
    }

    private void calculateSize() {
        //Для кнопки продолжить
        widthButtonResume = displayMetrics.widthPixels * percentage;
        double proportion = (double) buttonResume.getWidth() / buttonResume.getHeight();
        heightButtonResume = widthButtonResume / proportion;
        buttonResume = Bitmap.createScaledBitmap(buttonResume
                , (int) widthButtonResume, (int) heightButtonResume, true);

        //Для кнопки выйти
        widthButtonExit = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonExit.getWidth() / buttonExit.getHeight();
        heightButtonExit = widthButtonExit / proportion;
        buttonExit = Bitmap.createScaledBitmap(buttonExit
                , (int) widthButtonExit, (int) heightButtonExit, true);
    }

    private void calculateStartPosition() {
        xPositionButtonResume = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonResume = (int) (displayMetrics.heightPixels * 0.25);

        xPositionButtonExit = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonExit = (int) (displayMetrics.heightPixels * 0.4);
    }

    public void drawMenuButtons(Canvas canvas) {
        canvas.drawBitmap(buttonResume, xPositionButtonResume, yPositionButtonResume, null);
        canvas.drawBitmap(buttonExit, xPositionButtonExit, yPositionButtonExit, null);
    } //Рисуем кнопки в меню во время паузы.

    public void drawMenuEnd(Canvas canvas) {
        canvas.drawBitmap(buttonExit, xPositionButtonExit, yPositionButtonExit, null);
    } //Рисуем кнопки, когда игра завершена.

    public Enum onTouch(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchX >= xPositionButtonResume && touchX < (xPositionButtonResume + widthButtonResume) &&
                    touchY >= yPositionButtonResume && touchY < (yPositionButtonResume + heightButtonResume)) {
                return MenuActions.RESUME;
            }

            if (touchX >= xPositionButtonExit && touchX < (xPositionButtonExit + widthButtonExit) &&
                    touchY >= yPositionButtonExit && touchY < (yPositionButtonExit + heightButtonExit)) {
                return MenuActions.EXIT;
            }
        }

        return MenuActions.PENDING;
    }
}
