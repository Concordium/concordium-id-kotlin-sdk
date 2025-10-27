package com.concordium.sdk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.concordium.sdk.api.ConcordiumIDAppPopup
import com.concordium.sdk.app.ui.theme.ConcordiumSdkAppTheme
import com.concordium.sdk.app.ui.theme.Spacing
import com.concordium.sdk.app.ui.theme.Typography

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConcordiumSdkAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ConcordiumScreen(
                        content = stringResource(R.string.app_name),
                        callback = {},
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ConcordiumScreen(
    content: String,
    callback: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        callback()
    }
    var isCreateAccountChecked by rememberSaveable { mutableStateOf(false) }
    var isRecoverAccountChecked by rememberSaveable { mutableStateOf(false) }
    Column {
        Text(
            text = content,
            style = Typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = modifier.fillMaxWidth().padding(top = 64.dp)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(all = Spacing.fourX),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterVertically)
        ) {
            Button(onClick = { ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup() }) {
                Text(
                    text = stringResource(R.string.open_deeplink_popup),
                )
            }
            Spacer(Modifier.height(24.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCreateAccountChecked,
                        onCheckedChange = { isCreateAccountChecked = it })
                    Text(text = "Create account")
                    Spacer(Modifier.width(16.dp))
                    Checkbox(
                        checked = isRecoverAccountChecked,
                        onCheckedChange = { isRecoverAccountChecked = it })
                    Text(text = "Recover account")
                }

                Button(onClick = {
                    ConcordiumIDAppPopup.invokeIdAppActionsPopup(
                    )
                }) {
                    Text(
                        text = stringResource(R.string.open_actions_popup),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConcordiumSdkAppTheme {
        ConcordiumScreen(
            stringResource(R.string.app_name),
            callback = {}
        )
    }
}