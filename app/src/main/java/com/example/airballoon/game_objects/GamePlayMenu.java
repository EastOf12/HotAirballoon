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
import com.example.airballoon.models.User;

import java.util.HashMap;
import java.util.Map;

public class GamePlayMenu {
    Activity activity;
    DisplayMetrics displayMetrics;
    Bitmap buttonResume;
    Bitmap buttonExit;
    Bitmap buttonRestart;
    Bitmap buttonMarketingMoney;
    Bitmap buttonMarketingHp;
    int xPositionButtonResume;
    int yPositionButtonResume;

    int xPositionButtonExit;
    int yPositionButtonExit;
    int xPositionButtonRestart;
    int yPositionButtonRestart;
    int xPositionButtonMarketingMoney;
    int yPositionButtonMarketingMoney;
    int xPositionButtonMarketingHp;
    int yPositionButtonMarketingHp;
    double percentage = 0.25; // Размер изображения относительно экрана
    double widthButtonResume;
    double heightButtonResume;
    double widthButtonExit;
    double heightButtonExit;
    double widthButtonRestart;
    double heightButtonRestart;
    double widthButtonMarketingMoney;
    double heightButtonMarketingMoney;
    double widthButtonMarketingHp;
    double heightButtonMarketingHp;
    GameStatus gameStatus;
    HashMap<Integer, StarObject> stars = new HashMap<>();
    ButtonExit buttonEx;
    ButtonRestart buttonRest;
    ButtonNext buttonNext;
    ButtonMarketingMoney marketingMoney;
    ButtonMarketingHp marketingHp;
    BackGroundLevelCompleted backGroundLevelCompleted;
    ResultText resultText;
    private final int levelNumber;
    private int starCount;
    User user;

    Cube cube;

    public GamePlayMenu(Activity activity, DisplayMetrics displayMetrics, GameStatus gameStatus,
                        int levelNumber, User user) {
        this.activity = activity;
        this.displayMetrics = displayMetrics;
        this.gameStatus = gameStatus;
        this.levelNumber = levelNumber;
        this.user = user;

        buttonResume = BitmapFactory.decodeResource(activity.getResources(), R.drawable.resume);
        buttonExit = BitmapFactory.decodeResource(activity.getResources(), R.drawable.exit_game_play);
        buttonRestart = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.button_restart);
        buttonMarketingMoney = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.marketing_money_button);
        buttonMarketingHp = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.marketing_hp_button);

        calculateSize();
        calculateStartPosition();

        backGroundLevelCompleted = new BackGroundLevelCompleted(activity, displayMetrics,
                R.drawable.bg_level_completed, 1.5);
        cube = new Cube(activity, displayMetrics, R.drawable.cube,0.80);
        buttonEx = new ButtonExit(activity, displayMetrics, R.drawable.exit_game_play,
                0.15, cube);
        buttonRest = new ButtonRestart(activity, displayMetrics, R.drawable.button_restart,
                0.15, cube);

        buttonNext = new ButtonNext(activity, displayMetrics, R.drawable.next_level, 0.165,
                cube);
        marketingMoney = new ButtonMarketingMoney(activity, displayMetrics, R.drawable.marketing_money_button, 0.35,
                cube);
        marketingHp = new ButtonMarketingHp(activity, displayMetrics, R.drawable.marketing_hp_button, 0.35,
                cube);
    }

    public void reboot() {
        stars = new HashMap<>();
        starCount = 0;
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

        //Для кнопки рестарта
        widthButtonRestart = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonRestart.getWidth() / buttonRestart.getHeight();
        heightButtonRestart = widthButtonRestart / proportion;
        buttonRestart = Bitmap.createScaledBitmap(buttonRestart
                , (int) widthButtonRestart, (int) heightButtonRestart, true);

        //Для кнопки рекламы монетки
        widthButtonMarketingMoney = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonMarketingMoney.getWidth() / buttonMarketingMoney.getHeight();
        heightButtonMarketingMoney = widthButtonMarketingMoney / proportion;
        buttonMarketingMoney = Bitmap.createScaledBitmap(buttonMarketingMoney
                , (int) widthButtonMarketingMoney, (int) heightButtonMarketingMoney, true);

        //Для кнопки рекламы доп хп
        widthButtonMarketingHp = displayMetrics.widthPixels * percentage;
        proportion = (double) buttonMarketingHp.getWidth() / buttonMarketingHp.getHeight();
        heightButtonMarketingHp = widthButtonMarketingHp / proportion;
        buttonMarketingHp = Bitmap.createScaledBitmap(buttonMarketingHp
                , (int) widthButtonMarketingHp, (int) heightButtonMarketingHp, true);
    }

    private void calculateStartPosition() {
        xPositionButtonResume = (int) (displayMetrics.widthPixels * 0.4);
        yPositionButtonResume = (int) (displayMetrics.heightPixels * 0.25);

        xPositionButtonExit = (int) (displayMetrics.widthPixels * 0.25);
        yPositionButtonExit = (int) (displayMetrics.heightPixels * 0.7);

        xPositionButtonRestart = (int) (displayMetrics.widthPixels * 0.55);
        yPositionButtonRestart = (int) (displayMetrics.heightPixels * 0.6);

        xPositionButtonMarketingMoney = (int) (displayMetrics.widthPixels * 0.55);
        yPositionButtonMarketingMoney = (int) (displayMetrics.heightPixels * 0.6);

        xPositionButtonMarketingHp = (int) (displayMetrics.widthPixels * 0.55);
        yPositionButtonMarketingHp = (int) (displayMetrics.heightPixels * 0.6);
    }

    public void drawMenuButtons(Canvas canvas) {
        canvas.drawBitmap(buttonResume, xPositionButtonResume, yPositionButtonResume, null);
        canvas.drawBitmap(buttonExit, (int) (displayMetrics.widthPixels * 0.4),
                (int) (displayMetrics.heightPixels * 0.4), null);
    } //Рисуем кнопки в меню во время паузы.

    public void drawLevelCompleted(Canvas canvas, int starCount) {
        this.starCount = starCount;

        if(starCount == 0) {
            resultText = new ResultText(activity, displayMetrics, R.drawable.result_text_lose, 0.3, cube);
        } else {
            resultText = new ResultText(activity, displayMetrics, R.drawable.result_text, 0.3, cube);
        }

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
        marketingMoney.draw(canvas);

        if(starCount < 3) {
            marketingHp.draw(canvas);
        }

        if(levelNumber != 0 && (starCount > 0)) {
            buttonNext.draw(canvas);
        }
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
//                actions = MenuActions.EXIT;
            }

            //Обрабатываем нажатия на экране завершения уровня
            if (touchX >= buttonEx.xPosition && touchX < (buttonEx.xPosition + buttonEx.width)
                    && touchY >= buttonEx.yPosition && touchY < (buttonEx.yPosition
                    +  buttonEx.height) && (!isPaused || levelCompleted)) {
                actions = MenuActions.EXIT;
//                actions = MenuActions.EXIT;
            }

            if(touchX >= buttonRest.xPosition && touchX <(buttonRest.xPosition + buttonRest.width)
                    && touchY >= buttonRest.yPosition && touchY < ( buttonRest.yPosition
                    + buttonRest.height) && (!isPaused || levelCompleted)) {
                actions = MenuActions.RESTART;
            }

            if(touchX >= buttonNext.xPosition && touchX <(buttonNext.xPosition + buttonNext.width)
                    && touchY >= buttonNext.yPosition && touchY < ( buttonNext.yPosition
                    + buttonNext.height) && (!isPaused || levelCompleted)
                    && levelNumber != 0 && starCount > 0) {
                actions = MenuActions.NEXT;
            }

            if(touchX >= marketingMoney.xPosition && touchX <(marketingMoney.xPosition + marketingMoney.width)
                    && touchY >= marketingMoney.yPosition && touchY < ( marketingMoney.yPosition
                    + marketingMoney.height) && (!isPaused || levelCompleted)) {
                actions = MenuActions.MARKETING_MONEY;
            }

            if(touchX >= marketingHp.xPosition && touchX <(marketingHp.xPosition + marketingHp.width)
                    && touchY >= marketingHp.yPosition && touchY < ( marketingHp.yPosition
                    + marketingHp.height) && (!isPaused || levelCompleted) && starCount < 3) {
                actions = MenuActions.MARKETING_ADD_HP;
            }
        }

        return actions;
    }
}

class BackGroundLevelCompleted extends BaseObject {
    public BackGroundLevelCompleted(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(0, 0);
    }
}

class ResultText extends BaseObject {
    public ResultText(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.5 - width * 0.5),
                cube.getYPos() + (int) (cube.height * 0.15));
    }
}

class ButtonExit extends BaseObject {
    public ButtonExit(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.69),
                cube.getYPos() + (int) (cube.height * 0.5));
    }
}

class ButtonNext extends BaseObject {
    public ButtonNext(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.4),
                cube.getYPos() + (int) (cube.height * 0.48));
    }
}

class ButtonRestart extends BaseObject {
    public ButtonRestart(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                      double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.13),
                cube.getYPos() + (int) (cube.height * 0.5));
    }
}

class ButtonMarketingMoney extends BaseObject {
    public ButtonMarketingMoney(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                         double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.28),
                cube.getYPos() + (int) (cube.height * 1.2));
    }
}

class ButtonMarketingHp extends BaseObject {
    public ButtonMarketingHp(Activity activity, DisplayMetrics displayMetrics, int resourceId,
                                double percentage, Cube cube) {
        super(activity, displayMetrics, percentage);

        loadImage(activity, resourceId);
        calculateSize(displayMetrics);

        setPositions(cube.getXPos() + (int) (cube.width * 0.28),
                cube.getYPos() + (int) (cube.height * 1.6));
    }
}

class StarObject extends BaseObject{

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

class Cube extends BaseObject {

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
