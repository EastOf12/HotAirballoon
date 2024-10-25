package com.example.airballoon.managers;

import android.app.Activity;

import com.example.airballoon.models.User;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SaveManager {
    public static void save(Activity activity, User user) {
        String filename = "save.txt"; // имя файла, который нужно сохранить
        String content = user.toString(); // контент файла
        System.out.println("Пытаемся сохранить файл");

        try {
            File file = new File(activity.getFilesDir(), filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            System.out.println("Файл сохранен.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //Сохраняет прогресс в  файл

    public static User readFromFile(Activity activity) {
        String filename = "save.txt"; // имя файла, который нужно прочитать
        StringBuilder content = new StringBuilder();
        User user = null;

        try {
            File file = new File(activity.getFilesDir(), filename);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            // Создайте экземпляр Gson
            Gson gson = new Gson();

            // Десериализация объекта из строки
            System.out.println("content " + content);

            user = gson.fromJson(String.valueOf(content), User.class);

            //Если не получили информацию по выбранному шарику
            if(user.getSelectAirBalloon() == 0) {
                user.setSelectAirBalloon(1);
            }

            br.close();
            System.out.println("Прочитанный контент: " + content);

            System.out.println("Сам созданный класс " + user);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Пользователь которого вернули " + user);

        //Если нет файла сохранения, то создаем нового пользователя.
        if (user == null) {
            user = new User();
        }

        return user;
    } //Читает файл с сохранением
}
