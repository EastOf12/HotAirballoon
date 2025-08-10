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

import java.util.HashMap;
import java.util.Map;

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
    HashMap<Integer, StarObject> stars = new HashMap<>();
    ButtonExit buttonEx;
    ButtonRestart buttonRest;
    ButtonNext buttonNext;
    BackGroundLevelCompleted backGroundLevelCompleted;
    ResultText resultText;

    Cube cube;

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

        backGroundLevelCompleted = new BackGroundLevelCompleted(activity, displayMetrics,
                R.drawable.bg_level_completed, 1.5);
        cube = new Cube(activity, displayMetrics, R.drawable.cube,0.80);
        buttonEx = new ButtonExit(activity, displayMetrics, R.drawable.exit_game_play,
                0.15, cube);
        buttonRest = new ButtonRestart(activity, displayMetrics, R.drawable.button_restart,
                0.15, cube);

        buttonNext = new ButtonNext(activity, displayMetrics, R.drawable.next_level, 0.4,
                cube);

        resultText = new ResultText(activity, displayMetrics, R.drawable.result_text, 0.3, cube);
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

    public void drawLevelCompleted(Canvas canvas, int starCount) {
//        backGroundLevelCompleted.draw(canvas);

        if(stars.isEmpty()) {
            for(int i = 1; i < 4; i++) {
                StarObject starObject;

                double percentage;

                if (i == 2) {
                    percentage = 0.3;
                } else {
                    percentage = 0.2;
                }

                if (i <= starCount) {
                    starObject = new StarObject(activity, displayMetrics, R.drawable.star_yellow,
                            percentage, i, cube);
                } else {
                    starObject = new StarObject(activity, displayMetrics, R.drawable.star_brown,
                            percentage, i, cube);
                }

                stars.put(i, starObject);
            }
        }

        cube.draw(canvas);
        resultText.draw(canvas);

        for (Map.Entry<Integer, StarObject> st: stars.entrySet()) {
            st.getValue().draw(canvas);
        }

        //Рисуем кнопки
        buttonEx.draw(canvas);
        buttonRest.draw(canvas);
        buttonNext.draw(canvas);
    } //Рисуем экран пройденного уровня

    public Enum onTouch(MotionEvent event, boolean isPaused, boolean levelCompleted) {

        float touchX = event.getX();
        float touchY = event.getY();

        MenuActions actions = MenuActions.PENDING;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //Обрабатываем нажатия на экране паузы
            if (!levelCompleted && touchX >= xPositionButtonResume && touchX < (xPositionButtonResume + widthButtonResume) &&
                    touchY >= yPositionButtonResume && touchY < (yPositionButtonResume + heightButtonResume) && isPaused) {
                actions = MenuActions.RESUME;
            }

            if (!levelCompleted && touchX >= (int) (displayMetrics.widthPixels * 0.4) && touchX < ((int) (displayMetrics.widthPixels * 0.4) + widthButtonResume) &&
                    touchY >= (int) (displayMetrics.heightPixels * 0.4) && touchY < ((int) (displayMetrics.heightPixels * 0.4) + heightButtonResume)
            && isPaused) {
                actions = MenuActions.EXIT;
            }

            //Обрабатываем нажатия на экране завершения уровня
            if (touchX >= buttonEx.xPosition && touchX < (buttonEx.xPosition + buttonEx.width)
                    && touchY >= buttonEx.yPosition && touchY < (buttonEx.yPosition
                    +  buttonEx.height) && (!isPaused || levelCompleted)) {
                actions = MenuActions.EXIT;
            }

            if(touchX >= buttonRest.xPosition && touchX <(buttonRest.xPosition + buttonRest.width)
                    && touchY >= buttonRest.yPosition && touchY < ( buttonRest.yPosition
                    + buttonRest.height)) {
                actions = MenuActions.RESTART;
            }
        }

        return actions;
    }
}

class BackGroundLevelCompleted extends GamePlayMenuObject {
    public BackGroundLevelCompleted(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(0, 0);
    }
}

class ResultText extends GamePlayMenuObject {
    public ResultText(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.5 - width * 0.5),
                cube.getYPos() + (int) (cube.height * 0.15));
    }
}

class ButtonExit extends GamePlayMenuObject {
    public ButtonExit(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.65),
                cube.getYPos() + (int) (cube.height * 0.6));
    }
}

class ButtonNext extends GamePlayMenuObject {
    public ButtonNext(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.1),
                cube.getYPos() + (int) (cube.height * 0.4));
    }
}

class ButtonRestart extends GamePlayMenuObject {
    public ButtonRestart(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.75),
                cube.getYPos() + (int) (cube.height * 0.25));
    }
}

class StarObject extends GamePlayMenuObject{

    public StarObject(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, int starNumber, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        int yPos;
        int xPos;
        int cubeYPos = cube.getYPos();
        int cubeXPos = cube.getXPos();

        switch (starNumber) {
            case 1:
                yPos = cubeYPos - (int) (width / 2);
                xPos = cubeXPos + (int) (width * 0.2);
                break;
            case 2:
                yPos = cubeYPos - (int) (height * 0.85);
                xPos = (int) (cubeXPos + (cube.getWidth() * 0.5) - (width * 0.5));
                break;
            default:
                yPos = cubeYPos - (int) (width / 2);
                xPos = cubeXPos + (int) (cube.width * 0.7);
                break;
        }

        setPositions(xPos, yPos);
    }
}

class Cube extends GamePlayMenuObject {

    public Cube(Activity activity, DisplayMetrics displayMetrics, int resourceId, double percentage) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions((int) ((displayMetrics.widthPixels / 2) - (width / 2)),
                (int) ((displayMetrics.heightPixels / 2) - (height / 2)));
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

class GamePlayMenuObject{
    private Bitmap image;
    protected int xPosition;
    protected int yPosition;
    protected double width;
    protected double height;
    private final double percentage;
    Activity activity;
    DisplayMetrics displayMetrics;

    public GamePlayMenuObject(Activity activity, DisplayMetrics displayMetrics,
                              double percentage) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.percentage = percentage;
    }

    protected void loadImage(Activity activity, int resourceId) {
        image = BitmapFactory.decodeResource(activity.getResources(), resourceId);
    }

    protected void calculateSize(DisplayMetrics displayMetrics) {
        width = displayMetrics.widthPixels * percentage;
        double proportion = (double) image.getWidth() / image.getHeight();
        height = width / proportion;
        image = Bitmap.createScaledBitmap(image
                , (int) width, (int) height, true);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, xPosition, yPosition,
                null);
    }

    protected void setPositions(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
