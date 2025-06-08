package com.example.airballoon.managers;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.airballoon.R;

public class MediaPlayerSingleton {
    private static MediaPlayer mediaPlayer;
    private static boolean status = false;

    public static MediaPlayer getInstance(Context context) {
        if (!status) {
            mediaPlayer = MediaPlayer.create(context, R.raw.menu_music); // укажите свой аудиофайл
            mediaPlayer.setOnCompletionListener(mp -> mp.start()); // Зацикливание
            status = true;
        }
        return mediaPlayer;
    }

    public static void stop() {
        mediaPlayer.stop();
        status = false;
    }
}
