package com.concordium.sdk.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.concordium.sdk.R
import com.concordium.sdk.ui.model.StepItem
import com.concordium.sdk.ui.theme.ConcordiumSdkAppTheme

internal class ConcordiumSdkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConcordiumSdkAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SdkScreen(
                        onPopupClose = { this.finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SdkScreen(
    onPopupClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize(),
    ) {
        HeaderSection(onClose = onPopupClose)
        StepperView(
            modifier = modifier
                .wrapContentWidth()
                .background(Color.Cyan),
            items = listOf(
                StepItem(completed = true, label = "Step 1/6120739462"),
                StepItem(completed = false, label = "Step 2/@#@#@#@#@"),
                StepItem(completed = true, label = "Step 1/  445"),
                StepItem(completed = false, label = "Step 2/SSS"),
            ),
        )
//        QRCodeWebView("tyi")
        PlayStoreSection(infoText = stringResource(R.string.info_text_play_store))
        MatchCodeSection(
            instruction = stringResource(R.string.message_match_code_in_IDapp),
            codeText = "1234",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConcordiumSdkAppTheme {

    }
}