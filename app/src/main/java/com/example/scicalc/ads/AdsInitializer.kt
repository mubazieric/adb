package com.example.scicalc.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds

object AdsInitializer {
    fun initialize(context: Context) {
        MobileAds.initialize(context)
    }
}
