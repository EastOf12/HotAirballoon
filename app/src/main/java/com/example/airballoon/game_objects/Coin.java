package com.example.airballoon.game_objects;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import com.example.airballoon.managers.GamePlayManager;
import com.example.airballoon.R;

import java.util.Objects;
import java.util.Random;

public class Coin extends GameObject{
    Random random = new Random();
    AirBalloonObject airBalloon;

    private boolean needDraw;

    public Coin(Activity activity, DisplayMetrics displayMetrics, AirBalloonObject airBalloon) {
        super(activity, displayMetrics);

        this.airBalloon = airBalloon;
        needDraw = true;
        setPercentage(0.08);
        image = BitmapFactory.decodeResource(activity.getResources(), R.drawable.coin);
        calculateSize();
        calculateStartPosition();
        createRect();
    }

    @Override
    public void calculateStartPosition() {
        xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
        yPosition = -50;
        needDraw = true;
    }

    private void calculateNewPosition(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        }

        yPosition += GamePlayManager.speed;
    }


    @Override
    public void draw(Canvas canvas) {
        if(needDraw) {
            if(airBalloon.getHadMagnet() && needMagnetCoin()) { //Рассчитываем новую позицию монеток, когда включен магнит
                calculateNewPositionMagnet(canvas);
            } else {
                calculateNewPosition(canvas);
            }


            rect.left = xPosition;
            rect.top = yPosition;
            rect.right = (int) (xPosition + width);
            rect.bottom = (int) (yPosition + height);

            canvas.drawBitmap(image, xPosition, yPosition, null);
        }
    }

    public boolean checkCollisionAirBalloon() {

        boolean result = airBalloon.getRect().intersect(rect);

        if (result) {
            airBalloon.addCollectedCoins();
        }

        return result;
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return needDraw == coin.needDraw && Objects.equals(random, coin.random) && Objects.equals(airBalloon, coin.airBalloon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(random, airBalloon, needDraw);
    }

    private void calculateNewPositionMagnet(Canvas canvas) {
        if (yPosition >= canvas.getHeight() || checkCollisionAirBalloon()) {
            yPosition = random.nextInt(500) - 1000;
            xPosition = random.nextInt((int) (displayMetrics.widthPixels - width));
            needDraw = !needDraw;
        } else {
            int xAirballoonPosition = airBalloon.xPosition;
            int yAirballoonPosition = airBalloon.yPosition;

            //Изменяем позицию монетки ближе к шарику
            boolean needCalculate = !(yPosition > yAirballoonPosition && yPosition < yAirballoonPosition + airBalloon.height);

            if(needCalculate) {
                if(yPosition < yAirballoonPosition) {
                    yPosition += GamePlayManager.speed * 2.5;
                } else if(yPosition > yAirballoonPosition){
                    yPosition = (int) (yPosition - (GamePlayManager.speed * 2.5));
                }
            }

            needCalculate = !(xPosition > xAirballoonPosition && xPosition < xAirballoonPosition + (airBalloon.width * 0.5));

            if(needCalculate) {
                if(xPosition < xAirballoonPosition) {
                    xPosition += GamePlayManager.speed * 1.5;
                } else if (xPosition > xAirballoonPosition){
                    xPosition -= GamePlayManager.speed * 1.5;
                }
            }
        }
    }

    private boolean needMagnetCoin() {
        boolean xNeedMagnet = xPosition + (airBalloon.width * 2) > airBalloon.xPosition && xPosition < (airBalloon.xPosition + (airBalloon.width * 3));


        boolean yNeedMagnet = yPosition > (airBalloon.yPosition - airBalloon.height * 1.5) && yPosition < (airBalloon.yPosition + airBalloon.height * 2);

        return xNeedMagnet && yNeedMagnet;
    }

    public int getYPosition() {
        return yPosition;
    }
}
