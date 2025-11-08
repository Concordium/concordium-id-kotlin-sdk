package com.concordium.idapp.sdk.api

import android.content.Intent
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK.checkForInitialization
import com.concordium.idapp.sdk.common.Logger
import com.concordium.idapp.sdk.common.isValiWalletConnectUri
import com.concordium.idapp.sdk.common.isValidWalletConnectSessionTopic
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity
import com.concordium.idapp.sdk.ui.model.UserJourneyStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object ConcordiumIDAppPopup {

    internal val shouldCloseApp = MutableStateFlow(false)

    /**
     *  Invoke ID App Deep Link Popup
     *  @param walletConnectUri: String
     * */
    fun invokeIdAppDeepLinkPopup(
        walletConnectUri: String
    ) {
        checkForInitialization()
        require(walletConnectUri.isValiWalletConnectUri()) {
            "Invalid Wallet Connect's URI"
        }
        shouldCloseApp.update { false }
        Logger.d("Invoke ID App Deep Link Popup")

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

    /**
     * Invoke ID App Actions Popup
     *
     * @param walletConnectSessionTopic: String?
     * @param onCreateAccount: (() -> Unit)?
     * @param onRecoverAccount: (() -> Unit)?
     */
    fun invokeIdAppActionsPopup(
        walletConnectSessionTopic: String? = null,
        onCreateAccount: (() -> Unit)? = null,
        onRecoverAccount: (() -> Unit)? = null,
    ) {
        checkForInitialization()
        require(onCreateAccount != null || onRecoverAccount != null) {
            "At least one of the handlers must be provided"
        }
        if (onCreateAccount != null) {
            require(
                walletConnectSessionTopic != null && walletConnectSessionTopic.isValidWalletConnectSessionTopic()
            )
            { "Invalid Wallet Connect's session topic" }
        }
        shouldCloseApp.update { false }
        val action = when {
            onRecoverAccount == null -> "create"
            onCreateAccount == null -> "recover"
            else -> "create_or_recover"
        }
        Logger.d("Invoke ID App Actions Popup with action: $action")

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

    /**
     * Close ID App Popup
     */
    fun closePopup() {
        checkForInitialization()
        shouldCloseApp.update { true }
        Logger.d("Close ID App Popup")
    }
}