package com.concordium.sdk.app

import android.app.Application
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK

internal class ConcordiumSdkDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ConcordiumIDAppSDK.initialize(
            context = applicationContext,
            enableDebugLog = true,
        )
    }
}