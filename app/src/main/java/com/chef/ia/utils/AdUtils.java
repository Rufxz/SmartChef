package com.chef.ia.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chef.ia.R;
import com.chef.ia.activities.CreateRecipeActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdUtils {
    static RewardedAd rewardedAd;
    static boolean isRewardEarned;

    public static void initializeAdsAndShowBannerAd(Context context, AdView adView) {
        MobileAds.initialize(context, initializationStatus -> {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        });
    }

    public static void showRewardedAd(CreateRecipeActivity createRecipeActivity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(createRecipeActivity, createRecipeActivity.getResources().getString(R.string.rewarded_admob_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAd = null;
                        createRecipeActivity.getRecipe();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        rewardedAd.show(createRecipeActivity, rewardItem -> isRewardEarned = true);
                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                if (isRewardEarned) {
                                    createRecipeActivity.getRecipe();
                                } else {
                                    Toast.makeText(createRecipeActivity, "Please watch ad to create recipe", Toast.LENGTH_SHORT).show();
                                }
                                rewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                rewardedAd = null;
                                createRecipeActivity.getRecipe();
                            }
                        });
                    }
                });
    }
}
