package com.example.airballoon.managers;

import android.app.Activity;
import android.media.MediaPlayer;

import com.example.airballoon.R;

public class SoundManager {
    private final MediaPlayer coinSound;
    private final MediaPlayer damageSound;
    private final MediaPlayer shieldSound;
    private final MediaPlayer magnetSound;
    private final MediaPlayer shieldCrush;
    private final MediaPlayer levelCompleted;
    private final MediaPlayer bgSound;
    private final MediaPlayer pause;
    private boolean statusBgSound = true;

    public SoundManager(Activity activity) {
        coinSound = MediaPlayer.create(activity, R.raw.get_coin);
        damageSound = MediaPlayer.create(activity, R.raw.get_damage);
        shieldSound = MediaPlayer.create(activity, R.raw.get_shield);
        magnetSound = MediaPlayer.create(activity, R.raw.get_magnet);
        shieldCrush = MediaPlayer.create(activity, R.raw.shield_crush);
        levelCompleted = MediaPlayer.create(activity, R.raw.level_complited);
        pause = MediaPlayer.create(activity, R.raw.pause);
        bgSound = MediaPlayer.create(activity, R.raw.game_play_music);
    }

    //Звук сбора монетки
    public void getCoin() {
        if (coinSound != null) {
            if (coinSound.isPlaying()) {
                coinSound.seekTo(0);
            }

            coinSound.start();

            //В теории это надо, тк может есть производительность, пока не ощущаю, по этому не будем искать как это реализовать.
            coinSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    coinSound.release();
                }
            });
        }
    }

    //Фоновая мелодия
    public void getBgSound() {
        if (bgSound != null) {
            if (!bgSound.isPlaying()) {
                bgSound.start();
                bgSound.setVolume(0.3f, 0.3f);
            }
        }
    }

    public void stopBgSound() {
        if (bgSound != null) {
            bgSound.stop();
        }
    }

    //Получение урона
    public void getDamage() {
        if (damageSound != null) {

            if (damageSound.isPlaying()) {
                damageSound.seekTo(0);
            }

            damageSound.start();

            //В теории это надо, тк может есть производительность, пока не ощущаю, по этому не будем искать как это реализовать.
            damageSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    coinSound.release();
                }
            });
        }
    }

    public void getShield() {
        if (shieldSound != null) {

            if (shieldSound.isPlaying()) {
                shieldSound.seekTo(0);
            }

            shieldSound.start();

            //В теории это надо, тк может есть производительность, пока не ощущаю, по этому не будем искать как это реализовать.
            shieldSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    coinSound.release();
                }
            });
        }
    }

    public void shieldCrush() {
        if (shieldCrush != null) {

            if (shieldCrush.isPlaying()) {
                shieldCrush.seekTo(0);
            }

            shieldCrush.start();

            //В теории это надо, тк может есть производительность, пока не ощущаю, по этому не будем искать как это реализовать.
            shieldCrush.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    coinSound.release();
                }
            });
        }
    }

    public void getMagnet() {
        if (magnetSound != null) {

            if (magnetSound.isPlaying()) {
                magnetSound.seekTo(0);
            }

            magnetSound.start();

            //В теории это надо, тк может есть производительность, пока не ощущаю, по этому не будем искать как это реализовать.
            magnetSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
//                    coinSound.release();
                }
            });
        }
    }

    public void levelCompleted() {
        if (levelCompleted != null) {
            levelCompleted.start();

            if (statusBgSound) {
                bgSound.stop();
                statusBgSound = false;
            }

//            levelCompleted.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    levelCompleted.release();
//                    coinSound.release();
//                    damageSound.release();
//                    shieldSound.release();
//                    magnetSound.release();
//                    shieldCrush.release();
//                }
//            });
        }
    }

    public void pause() {
        if (pause != null) {
            pause.setVolume(0.5f, 0.5f);

            if (pause.isPlaying()) {
                pause.seekTo(0);
            }

            pause.start();

            pause.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });
        }
    }
}
