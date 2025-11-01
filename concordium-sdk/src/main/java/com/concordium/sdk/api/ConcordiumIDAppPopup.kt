package com.concordium.sdk.api

import android.content.Intent
import com.concordium.sdk.api.ConcordiumIDAppSDK.checkForInitialization
import com.concordium.sdk.ui.ConcordiumSdkActivity
import com.concordium.sdk.ui.model.UserJourneyStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object ConcordiumIDAppPopup {

    internal val shouldCloseApp = MutableStateFlow(false)

    fun invokeIdAppDeepLinkPopup(
        walletConnectUri: String
    ) {
        checkForInitialization()
        shouldCloseApp.update { false }
        val context = ConcordiumIDAppSDK.context
        val intent = ConcordiumSdkActivity.createIntent(
            context = context,
            step = UserJourneyStep.Connect.name,
            walletConnectUri = walletConnectUri,
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun invokeIdAppActionsPopup(
        walletConnectSessionTopic: String? = null,
        onCreateAccount: (() -> Unit)? = null,
        onRecoverAccount: (() -> Unit)? = null,
    ) {
        checkForInitialization()
        shouldCloseApp.update { false }
        if (onCreateAccount == null && onRecoverAccount == null) {
            throw IllegalArgumentException("At least one of the handlers must be provided")
        }
        if (onCreateAccount != null && walletConnectSessionTopic == null) {
            throw IllegalArgumentException("Wallet Connect's session topic is required");
        }
        val action = when {
            onRecoverAccount == null -> "create"
            onCreateAccount == null -> "recover"
            else -> "create_or_recover"
        }
        val code = walletConnectSessionTopic?.substring(0, 4)?.uppercase()
        val context = ConcordiumIDAppSDK.context
        val intent = ConcordiumSdkActivity.createIntent(
            context = context,
            action = action,
            step = UserJourneyStep.IdVerification.name,
            code = code.orEmpty(),
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun closePopup() {
        checkForInitialization()
        shouldCloseApp.update { true }
    }
}