package com.example.airballoon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.airballoon.databinding.ActivityRewardedAdBinding;
import com.example.airballoon.managers.SaveManager;
import com.example.airballoon.models.User;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.rewarded.Reward;
import com.yandex.mobile.ads.rewarded.RewardedAd;
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener;
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener;
import com.yandex.mobile.ads.rewarded.RewardedAdLoader;

public class RewardedAdActivity extends AppCompatActivity {
    @Nullable
    private RewardedAd mRewardedAd = null;
    @Nullable
    private RewardedAdLoader mRewardedAdLoader = null;
    ActivityRewardedAdBinding mBinding;
    Activity activity;

    public RewardedAdActivity() {
        super(R.layout.activity_rewarded_ad);
        activity = this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("Запустили рут метод отображения рекламы");
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MobileAds.initialize(this, () -> {
            // now you can use ads
        });

        mBinding = ActivityRewardedAdBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        // Загрузка рекламы должна происходить после инициализации SDK.
        // Инициализируйте SDK как можно раньше, например, в приложении.onCreate или Activity.onCreate
        mRewardedAdLoader = new RewardedAdLoader(this);
        mRewardedAdLoader.setAdLoadListener(new RewardedAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
                // Реклама была успешно загружена. Теперь вы можете показывать загруженную рекламу.
                System.out.println("Показываем объявление");
                showAd();
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Не удалось загрузить объявление с помощью AdRequestError.
                // Настоятельно не рекомендуется пытаться загрузить новое объявление с помощью метода onAdFailedToLoad().

                System.out.println("Не смогли загрузить объявление");
                // Закрыть текущую активность
                finish();

                // Открыть основную активность
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
            }
        });
        loadRewardedAd();
    }

    private void loadRewardedAd() {
        if (mRewardedAdLoader != null ) {
            System.out.println("Загрузили новый рекламный блок");
            final AdRequestConfiguration adRequestConfiguration =
                    new AdRequestConfiguration.Builder("demo-rewarded-yandex").build(); //Тестовый, его нужно будет заменить
            //                    new AdRequestConfiguration.Builder("R-M-11206968-1").build();
            mRewardedAdLoader.loadAd(adRequestConfiguration);
        }
    }

    private void showAd() {
        if (mRewardedAd != null) {
            mRewardedAd.setAdEventListener(new RewardedAdEventListener() {
                @Override
                public void onAdShown() {
                    // Вызывается при показе рекламы.
                }

                @Override
                public void onAdFailedToShow(@NonNull final AdError adError) {
                    // Вызывался, когда реклама не показывалась.

                    // Очистить ресурсы после сбоя показа рекламы
                    if (mRewardedAd != null) {
                        mRewardedAd.setAdEventListener(null);
                        mRewardedAd = null;
                    }

                    // Теперь вы можете предварительно загрузить следующее объявление.
                    loadRewardedAd();
                }

                @Override
                public void onAdDismissed() {
                    // Вызывается, когда объявление закрывается.
                    System.out.println("Хочет закрыть объявление");

                    // Закрыть текущую активность
                    finish();

                    // Открыть основную активность
                    Intent intent = new Intent(activity, MainActivity.class);
                    startActivity(intent);

                    //Будто бы это не надо (Может пригодиться только для оптимизации, чтобы
                    // не создавать активность с рекламой каждый раз после завершения игры

                    // Очистить ресурсы после удаления рекламы
                    if (mRewardedAd != null) {
                        mRewardedAd.setAdEventListener(null);
                        mRewardedAd = null;
                    }

                    // Теперь вы можете предварительно загрузить следующее объявление.
//                    loadRewardedAd();
                }

                @Override
                public void onAdClicked() {
                    // Вызывается, когда регистрируется клик по объявлению.
                    System.out.println("Клик по объявлению");
                }

                @Override
                public void onAdImpression(@Nullable final ImpressionData impressionData) {
                    // Вызывается при регистрации показа рекламы.
                    System.out.println("Регистрация показа рекламы");
                }

                @Override
                public void onRewarded(@NonNull final Reward reward) {
                    // Вызывается, когда пользователь может быть вознагражден.
                   System.out.println("Показали рекламу");

                   //Начисляем награду пользвателю
                    User user = SaveManager.readFromFile(activity);
                    user.addCoins(user.getLastCoins());
                    SaveManager.save(activity, user);
                    System.out.println("Начислили награду пользователю " + user.getLastCoins());

                }
            });

            mRewardedAd.show(this);
        }
    }


    //Освобождение ресурсов
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRewardedAdLoader != null) {
            mRewardedAdLoader.setAdLoadListener(null);
            mRewardedAdLoader = null;
        }
        destroyRewardedAd();
    }

    private void destroyRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.setAdEventListener(null);
            mRewardedAd = null;
        }
    }
}