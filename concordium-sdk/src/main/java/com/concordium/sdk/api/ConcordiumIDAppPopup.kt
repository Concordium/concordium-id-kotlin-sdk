package com.concordium.sdk.api

import android.content.Intent
import com.concordium.sdk.ui.ConcordiumSdkActivity

object ConcordiumIDAppPopup {

    fun invokeIdAppDeepLinkPopup() {
        val context = ConcordiumIDAppSDK.context
        val intent = Intent(
            context,
            ConcordiumSdkActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun invokeIdAppActionsPopup(
        onCreate: (() -> Unit)? = null,
        onRecover: (() -> Unit)? = null,
    ) {
        val context = ConcordiumIDAppSDK.context
        val intent = Intent(
            context,
            ConcordiumSdkActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}