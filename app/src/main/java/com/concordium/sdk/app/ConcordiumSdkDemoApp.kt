package com.concordium.sdk.app

import android.app.Application
import com.concordium.sdk.api.ConcordiumIDAppSDK

class ConcordiumSdkDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ConcordiumIDAppSDK.initialize(
            context = applicationContext,
            configuration = ConcordiumIDAppSDK.Configuration.TEST
        )
    }
}