package com.hoangndang.femo;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by hdang on 3/29/2016.
 */
public class AdUtilities {
    public static void requestNewInterstitial(InterstitialAd mInterstitialAd){
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("A438C857BE7280881A01BEFCBC85A612") //add device that should get test ads //A438C857BE7280881A01BEFCBC85A612 - my S6
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}
