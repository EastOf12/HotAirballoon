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
    Bitmap buttonRestart;
    int xPositionButtonResume;
    int yPositionButtonResume;

    int xPositionButtonExit;
    int yPositionButtonExit;
    int xPositionButtonRestart;
    int yPositionButtonRestart;
    double percentage = 0.25; // Размер изображения относительно экрана
    double widthButtonResume;
    double heightButtonResume;
    double widthButtonExit;
    double heightButtonExit;
    double widthButtonRestart;
    double heightButtonRestart;
    GameStatus gameStatus;

    public GamePlayMenu(Activity activity, DisplayMetrics displayMetrics, GameStatus gameStatus) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.gameStatus = gameStatus;

        buttonResume = BitmapFactory.decodeResource(activity.getResources(), R.drawable.resume);
        buttonExit = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit_game_play);
        buttonRestart = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.button_restart);

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
        widthButtonRestart = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonRestart.getWidth() / buttonRestart.getHeight();
        heightButtonRestart = widthButtonRestart / proportion;
        buttonRestart = Bitmap.createScaledBitmap(buttonRestart
                , (int) widthButtonRestart, (int) heightButtonRestart, true);
    }

    private void calculateStartPosition() {
        xPositionButtonResume = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonResume = (int) (displayMetrics.heightPixels * 0.25);

        xPositionButtonExit = (int) (displayMetrics.widthPixels * 0.25);
        yPositionButtonExit = (int) (displayMetrics.heightPixels * 0.7);

        xPositionButtonRestart = (int) (displayMetrics.widthPixels * 0.55);
        yPositionButtonRestart = (int) (displayMetrics.heightPixels * 0.6);
    }

    public void drawMenuButtons(Canvas canvas) {
        canvas.drawBitmap(buttonResume, xPositionButtonResume, yPositionButtonResume, null);
        canvas.drawBitmap(buttonExit, (int) (displayMetrics.widthPixels * 0.4),
                (int) (displayMetrics.heightPixels * 0.4), null);
    } //Рисуем кнопки в меню во время паузы.

    public void drawMenuEnd(Canvas canvas) {
        //Кнопка выхода в меню
        canvas.drawBitmap(buttonExit, xPositionButtonExit, yPositionButtonExit, null);

        //Кнопка перезапуска
        canvas.drawBitmap(buttonRestart, xPositionButtonRestart, yPositionButtonRestart,
                null);
    } //Рисуем кнопки, когда игра завершена.

    public Enum onTouch(MotionEvent event, boolean isPaused) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchX >= xPositionButtonResume && touchX < (xPositionButtonResume + widthButtonResume) &&
                    touchY >= yPositionButtonResume && touchY < (yPositionButtonResume + heightButtonResume) && isPaused) {
                return MenuActions.RESUME;
            }

            if (touchX >= (int) (displayMetrics.widthPixels * 0.4) && touchX < ((int) (displayMetrics.widthPixels * 0.4) + widthButtonResume) &&
                    touchY >= (int) (displayMetrics.heightPixels * 0.4) && touchY < ((int) (displayMetrics.heightPixels * 0.4) + heightButtonResume)
            && isPaused) {
                return MenuActions.EXIT;
            }

            if (touchX >= xPositionButtonExit && touchX < (xPositionButtonExit + widthButtonExit) &&
                    touchY >= yPositionButtonExit && touchY < (yPositionButtonExit + heightButtonExit) && !isPaused) {
                return MenuActions.EXIT;
            }

            if(touchX >= xPositionButtonRestart && touchX <(xPositionButtonRestart
                    + widthButtonRestart) && touchY >= yPositionButtonRestart &&
                    touchY < (yPositionButtonRestart + heightButtonRestart)) {
                return MenuActions.RESTART;
            }
        }

        return MenuActions.PENDING;
    }
}
