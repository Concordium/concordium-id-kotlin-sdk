package com.concordium.sdk.app

import android.app.Application
import com.concordium.sdk.api.ConcordiumIDAppSDK
import com.concordium.sdk.api.Configuration

class ConcordiumSdkDemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ConcordiumIDAppSDK.initialize(
            context = applicationContext,
            configuration = Configuration.TEST
        )
    }
}