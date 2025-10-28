package com.concordium.sdk.api

import android.content.Intent
import com.concordium.sdk.ui.ConcordiumSdkActivity
import com.concordium.sdk.ui.model.UserJourneyStep

object ConcordiumIDAppPopup {

    fun invokeIdAppDeepLinkPopup() {
        val context = ConcordiumIDAppSDK.context
        val intent = ConcordiumSdkActivity.createIntent(
            context = context,
            step = UserJourneyStep.IdVerification.name,
            code = "23DE"
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun invokeIdAppActionsPopup(
        code: String? = null,
        onCreate: (() -> Unit)? = null,
        onRecover: (() -> Unit)? = null,
    ) {
        require(onCreate != null || onRecover != null) {
            "At least one action must be provided"
        }
        val action = when {
            onCreate != null && onRecover != null -> "create_or_recover"
            onCreate != null -> "create"
            else -> "recover"
        }
        val context = ConcordiumIDAppSDK.context
        val intent = ConcordiumSdkActivity.createIntent(
            context = context,
            action = action,
            code = code.orEmpty(),
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}