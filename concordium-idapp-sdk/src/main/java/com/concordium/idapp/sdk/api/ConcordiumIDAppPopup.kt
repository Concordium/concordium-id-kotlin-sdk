package com.concordium.idapp.sdk.api

import android.content.Intent
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK.checkForInitialization
import com.concordium.idapp.sdk.common.Constants.REQUEST_VP_V1
import com.concordium.idapp.sdk.common.Logger
import com.concordium.idapp.sdk.common.isValiWalletConnectUri
import com.concordium.idapp.sdk.common.isValidWalletConnectSessionTopic
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity
import com.concordium.idapp.sdk.ui.model.UserJourneyStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

object ConcordiumIDAppPopup {

    internal val shouldCloseApp = MutableStateFlow(false)
    internal var onCreateAccountCallbackHolder: () -> Unit = {}
    internal var onGenerateProofCallbackHolder: () -> Unit = {}

    /**
     *  Invoke ID App Deep Link Popup
     *
     *  @param walletConnectUri: String - Valid WalletConnect URI starting with "wc:"
     *  @throws IllegalArgumentException if URI format is invalid
     *  @throws IllegalStateException if SDK not initialized
     * */
    fun invokeIdAppDeepLinkPopup(
        walletConnectUri: String,
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
     * @param walletConnectSessionTopic: String? - Valid WalletConnect session topic
     * @param onCreateAccount: (() -> Unit) - Handler when user selects create account
     */
    fun invokeIdAppActionsPopup(
        walletConnectSessionTopic: String,
        onCreateAccount: (() -> Unit),
    ) {
        invokeIdAppActionsPopup(
            walletConnectSessionTopic = walletConnectSessionTopic,
            requestMethod = "",
            onCreateAccount = onCreateAccount,
            onGenerateProof = {},
        )
    }

    /**
     * Invoke ID App Actions Popup with request method support
     *
     * @param walletConnectSessionTopic: String - Valid WalletConnect session topic
     * @param requestMethod: String - Request type, e.g. request_verifiable_presentation_v1
     * @param onCreateAccount: (() -> Unit) - Handler for account creation request
     * @param onGenerateProof: (() -> Unit) - Handler for proof generation request
     */
    fun invokeIdAppActionsPopup(
        walletConnectSessionTopic: String,
        requestMethod: String,
        onCreateAccount: (() -> Unit) = {},
        onGenerateProof: (() -> Unit) = {},
    ) {
        checkForInitialization()
        require(walletConnectSessionTopic.isValidWalletConnectSessionTopic()) {
            "Invalid Wallet Connect's session topic"
        }
        shouldCloseApp.update { false }
        onCreateAccountCallbackHolder = onCreateAccount
        onGenerateProofCallbackHolder = onGenerateProof

        val action = if (requestMethod == REQUEST_VP_V1) "generate-proof" else "create"
        Logger.d("Invoke ID App Actions Popup with action: $action")

        val code = walletConnectSessionTopic.take(4).uppercase()
        val context = ConcordiumIDAppSDK.context
        val intent = ConcordiumSdkActivity.createIntent(
            context = context,
            step = UserJourneyStep.IdVerification.name,
            code = code,
            requestMethod = requestMethod,
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Close ID App Popup
     */
    fun closePopup() {
        onCreateAccountCallbackHolder = {}
        onGenerateProofCallbackHolder = {}
        shouldCloseApp.update { true }
        Logger.d("Close ID App Popup")
    }
}