package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.example.airballoon.managers.GameStatus;
import com.example.airballoon.managers.MenuActions;
import com.example.airballoon.R;

public class GamePlayMenu {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap buttonResume;
    Bitmap buttonExit;
    Bitmap buttonMarketing;
    int xPositionButtonResume;
    int yPositionButtonResume;

    int xPositionButtonExit;
    int yPositionButtonExit;
    int xPositionButtonMarketing;
    int yPositionButtonMarketing;
    double percentage = 0.25; // Размер изображения относительно экрана
    double widthButtonResume;
    double heightButtonResume;
    double widthButtonExit;
    double heightButtonExit;
    double widthButtonMarketing;
    double heightButtonMarketing;
    GameStatus gameStatus;

    public GamePlayMenu(Activity activity, DisplayMetrics displayMetrics, GameStatus gameStatus) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.gameStatus = gameStatus;

        buttonResume = BitmapFactory.decodeResource(activity.getResources(), R.drawable.resume);
        buttonExit = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit_game_play);
        buttonMarketing = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.button_marketing);

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

        //Для кнопки рекламы
        widthButtonMarketing = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonMarketing.getWidth() / buttonMarketing.getHeight();
        heightButtonMarketing = widthButtonMarketing / proportion;
        buttonMarketing = Bitmap.createScaledBitmap(buttonMarketing
                , (int) widthButtonMarketing, (int) heightButtonMarketing, true);
    }

    private void calculateStartPosition() {
        xPositionButtonResume = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonResume = (int) (displayMetrics.heightPixels * 0.25);

        xPositionButtonExit = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonExit = (int) (displayMetrics.heightPixels * 0.4);

        xPositionButtonMarketing = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonMarketing = (int) (displayMetrics.heightPixels * 0.6);
    }

    public void drawMenuButtons(Canvas canvas) {
        canvas.drawBitmap(buttonResume, xPositionButtonResume, yPositionButtonResume, null);
        canvas.drawBitmap(buttonExit, xPositionButtonExit, yPositionButtonExit, null);
    } //Рисуем кнопки в меню во время паузы.

    public void drawMenuEnd(Canvas canvas) {
        //Кнопка выхода в меню
        canvas.drawBitmap(buttonExit, xPositionButtonExit, yPositionButtonExit, null);

        //Кнопка рекламы
        canvas.drawBitmap(buttonMarketing, xPositionButtonMarketing, yPositionButtonMarketing,
                null);
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

            if(touchX >= xPositionButtonMarketing && touchX <(xPositionButtonMarketing
                    + widthButtonMarketing) && touchY >= yPositionButtonMarketing &&
                    touchY < (yPositionButtonMarketing + heightButtonMarketing)) {
                return MenuActions.MARKETING;
            }
        }

        return MenuActions.PENDING;
    }
}
