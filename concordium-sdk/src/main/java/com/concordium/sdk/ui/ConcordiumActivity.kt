@file:OptIn(ExperimentalMaterial3Api::class)

package com.concordium.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.concordium.sdk.R
import com.concordium.sdk.ui.model.AccountAction
import com.concordium.sdk.ui.model.StepItem
import com.concordium.sdk.ui.model.UiState
import com.concordium.sdk.ui.model.UserJourneyStep
import com.concordium.sdk.ui.model.UserJourneyStep.Connect
import com.concordium.sdk.ui.theme.ConcordiumSdkAppTheme

internal class ConcordiumSdkActivity : ComponentActivity() {

    companion object {
        const val KEY_ACTION = "key_action"
        const val KEY_STEP = "key_step"
        const val KEY_CODE = "key_code"

        fun createIntent(
            context: Context,
            action: String = "create_or_recover",
            step: String = Connect.name,
            code: String? = null,
        ) =
            Intent(context, ConcordiumSdkActivity::class.java).apply {
                putExtra(KEY_ACTION, action)
                putExtra(KEY_STEP, step)
                putExtra(KEY_CODE, code)
            }
    }

    private val viewModel: ConcordiumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            viewModel.initialize(intent)
        }
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ConcordiumSdkAppTheme {
                SdkBottomSheet(
                    uiState = uiState,
                    onPopupClose = { this.finish() },
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
}

@Composable
internal fun SdkBottomSheet(
    uiState: UiState,
    onPopupClose: () -> Unit,
    modifier: Modifier = Modifier,
    onCreate: () -> Unit = {},
    onRecover: () -> Unit = {},
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { },
        sheetState = sheetState,
        modifier = Modifier.wrapContentSize() // 90% of screen height
    ) {
        SdkScreen(
            uiState = uiState,
            onPopupClose = onPopupClose,
            modifier = modifier,
            onCreate = onCreate,
            onRecover = onRecover,
        )
    }
}

@Composable
internal fun SdkScreen(
    uiState: UiState,
    onPopupClose: () -> Unit,
    modifier: Modifier = Modifier,
    onCreate: () -> Unit = {},
    onRecover: () -> Unit = {},
) {
    Column(
        modifier.fillMaxWidth(),
    ) {
        HeaderSection(onClose = onPopupClose)
        StepperSection(
            currentStep = uiState.journeyStep,
            accountAction = uiState.accountAction,
        )
        ContentSection(
            userJourneyStep = uiState.journeyStep,
            accountAction = uiState.accountAction,
            onCreate = onCreate,
            onRecover = onRecover,
        )
        BottomSection(
            step = uiState.journeyStep,
            accountAction = uiState.accountAction
        )
    }
}

@Composable
internal fun ContentSection(
    userJourneyStep: UserJourneyStep,
    accountAction: AccountAction,
    onCreate: () -> Unit = {},
    onRecover: () -> Unit = {},
) {
    when (userJourneyStep) {
        Connect -> {
            QRCodeSection(deepLinkInvoke = {})
        }

        UserJourneyStep.IdVerification -> IdVerificationSection(
            accountAction = accountAction,
            onCreate = onCreate,
            onRecover = onRecover
        )

        UserJourneyStep.Account -> {}
    }
}

@Composable
internal fun BottomSection(
    step: UserJourneyStep,
    accountAction: AccountAction,
    modifier: Modifier = Modifier
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
    currentStep: UserJourneyStep,
    accountAction: AccountAction,
    modifier: Modifier = Modifier
) {
    StepperView(
        modifier = modifier
            .wrapContentWidth(),
        items = listOf(
            StepItem(selected = true, label = stringResource(R.string.step_connect_pair_app)),
            StepItem(
                selected = UserJourneyStep.IdVerification <= currentStep,
                label = stringResource(R.string.step_complete_id_verification)
            ),
            StepItem(
                selected = UserJourneyStep.Account <= currentStep, label = when (accountAction) {
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