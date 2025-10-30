@file:OptIn(ExperimentalMaterial3Api::class)

package com.concordium.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.concordium.sdk.R
import com.concordium.sdk.api.ConcordiumIDAppPopup
import com.concordium.sdk.common.Constants
import com.concordium.sdk.common.handleAppDeepLink
import com.concordium.sdk.ui.model.AccountAction
import com.concordium.sdk.ui.model.StepItem
import com.concordium.sdk.ui.model.UiState
import com.concordium.sdk.ui.model.UserJourneyStep
import com.concordium.sdk.ui.model.UserJourneyStep.Connect
import com.concordium.sdk.ui.theme.ConcordiumSdkAppTheme
import kotlinx.coroutines.launch

internal class ConcordiumSdkActivity : ComponentActivity() {

    companion object {
        const val KEY_ACTION = "key_action"
        const val KEY_STEP = "key_step"
        const val KEY_CODE = "key_code"
        const val KEY_URI = "key_uri"

        fun createIntent(
            context: Context,
            step: String,
            action: String = "create_or_recover",
            code: String? = null,
            walletConnectUri: String? = null,
        ) = Intent(context, ConcordiumSdkActivity::class.java).apply {
            putExtra(KEY_ACTION, action)
            putExtra(KEY_STEP, step)
            putExtra(KEY_CODE, code)
            putExtra(KEY_URI, walletConnectUri)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
    }

    private val viewModel: ConcordiumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            ConcordiumIDAppPopup.shouldCloseApp.collect {
                if (it) {
                    finish()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                0,
                0,
            )
        } else {
            overridePendingTransition(0, 0)
        }

        if (savedInstanceState == null) {
            viewModel.initialize(intent)
        }
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ConcordiumSdkAppTheme {
                SdkBottomSheet(
                    uiState = uiState,
                    onPopupClose = { this@ConcordiumSdkActivity.finish() },
                )
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    SdkScreen(
//                        uiState = uiState,
//                        onPopupClose = { this.finish() },
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE, 0, // Enter animation
                0  // Exit animation
            )
        } else {
            overridePendingTransition(0, 0)
        }
    }
}

@Composable
internal fun SdkBottomSheet(
    uiState: UiState,
    onPopupClose: () -> Unit,
    modifier: Modifier = Modifier,
    showBottomSheet: Boolean = true,
    onCreateAccount: () -> Unit = {},
    onRecoverAccount: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onPopupClose,
            sheetState = sheetState,
            dragHandle = null,
            modifier = Modifier.wrapContentSize() // 90% of screen height
        ) {
            BackHandler(enabled = true) {
                // Do nothing (ignore back press)
            }
            SdkScreen(
                uiState = uiState,
                onPopupClose = onPopupClose,
                modifier = modifier,
                onCreateAccount = onCreateAccount,
                onRecoverAccount = onRecoverAccount,
            )
        }
    }
}

@Composable
internal fun SdkScreen(
    uiState: UiState,
    onPopupClose: () -> Unit,
    modifier: Modifier = Modifier,
    onCreateAccount: () -> Unit = {},
    onRecoverAccount: () -> Unit = {},
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(White),
    ) {
        HeaderSection(onClose = onPopupClose)
        StepperSection(
            currentStep = uiState.journeyStep,
            accountAction = uiState.accountAction,
        )
        ContentSection(
            userJourneyStep = uiState.journeyStep,
            accountAction = uiState.accountAction,
            walletConnectUri = uiState.walletConnectUri,
            onCreateAccount = onCreateAccount,
            onRecoverAccount = onRecoverAccount,
        )
        BottomSection(
            step = uiState.journeyStep, accountAction = uiState.accountAction
        )
    }
}

@Composable
internal fun ContentSection(
    userJourneyStep: UserJourneyStep,
    accountAction: AccountAction,
    walletConnectUri: String = "",
    onCreateAccount: () -> Unit = {},
    onRecoverAccount: () -> Unit = {},
) {
    val context = LocalContext.current
    when (userJourneyStep) {
        Connect -> {
            QRCodeSection(walletConnectUri = walletConnectUri, deepLinkInvoke = {
                handleAppDeepLink("${Constants.MOBILE_URI_LINK}$walletConnectUri", context)
            })
        }

        UserJourneyStep.IdVerification -> IdVerificationSection(
            accountAction = accountAction, onCreateAccount = onCreateAccount, onRecoverAccount = onRecoverAccount
        )

        UserJourneyStep.Account -> {}
    }
}

@Composable
internal fun BottomSection(
    step: UserJourneyStep, accountAction: AccountAction, modifier: Modifier = Modifier
) {
    when (step) {
        Connect -> PlayStoreSection(infoText = stringResource(R.string.info_text_play_store))
        UserJourneyStep.IdVerification -> {
            when (accountAction) {
                is AccountAction.Create -> MatchCodeSection(
                    instruction = stringResource(R.string.message_match_code_in_IDapp),
                    codeText = accountAction.code,
                )

                is AccountAction.CreateOrRecover -> MatchCodeSection(
                    instruction = stringResource(R.string.message_match_code_in_IDapp),
                    codeText = accountAction.code,
                )

                else -> {}
            }
        }

        UserJourneyStep.Account -> {}
    }
}

@Composable
internal fun StepperSection(
    currentStep: UserJourneyStep, accountAction: AccountAction, modifier: Modifier = Modifier
) {
    StepperView(
        modifier = modifier.wrapContentWidth(),
        items = listOf(
            StepItem(selected = true, label = stringResource(R.string.step_connect_pair_app)),
            StepItem(
                selected = UserJourneyStep.IdVerification <= currentStep,
                label = stringResource(R.string.step_complete_id_verification)
            ),
            StepItem(
                selected = UserJourneyStep.Account <= currentStep,
                label = when (accountAction) {
                    AccountAction.Recover -> stringResource(R.string.step_recover_account)
                    is AccountAction.Create -> stringResource(R.string.step_create_account)
                    else -> stringResource(R.string.step_create_recover_account)
                }
            ),
        ),
    )

}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    ConcordiumSdkAppTheme {

    }
}