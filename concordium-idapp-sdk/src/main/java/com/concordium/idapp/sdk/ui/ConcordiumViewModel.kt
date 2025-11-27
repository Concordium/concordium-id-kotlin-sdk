package com.concordium.idapp.sdk.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity.Companion.KEY_ACTION
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity.Companion.KEY_CODE
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity.Companion.KEY_STEP
import com.concordium.idapp.sdk.ui.ConcordiumSdkActivity.Companion.KEY_URI
import com.concordium.idapp.sdk.ui.model.AccountAction
import com.concordium.idapp.sdk.ui.model.UiState
import com.concordium.idapp.sdk.ui.model.UserJourneyStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ConcordiumViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState: StateFlow<UiState?> = _uiState.asStateFlow()

    fun initialize(intent: Intent) {
        val actionData = intent.getStringExtra(KEY_ACTION)
        val stepData = intent.getStringExtra(KEY_STEP)
        val codeData = intent.getStringExtra(KEY_CODE).orEmpty()
        val wcUri = intent.getStringExtra(KEY_URI).orEmpty()

        _uiState.update {
            UiState(
                accountAction = AccountAction.from(action = actionData, code = codeData),
                journeyStep = UserJourneyStep.from(stepData),
                walletConnectUri = wcUri,
            )
        }
    }
}