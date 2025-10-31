package com.concordium.sdk.app

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.concordium.sdk.api.ConcordiumIDAppPopup
import com.concordium.sdk.app.ui.theme.ConcordiumSdkAppTheme
import com.concordium.sdk.app.ui.theme.Spacing
import com.concordium.sdk.app.ui.theme.Typography

private const val walletConnectUri =
    "wc:2b4e5df1-91e3-4c62-9d0a-dc2318a1f2d2@2?relay-protocol=irn&symKey=dcf9e8f542e24435b7d4a6785a1e8b32e2b03728f6b6a8a5c6e4d1b6b3a9d8cf"
private const val walletConnectSessionTopic = "dcf9e8f542e24435b7d4a6785a1e8b32e2b"

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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        callback()
    }
    var isCreateAccountChecked by rememberSaveable { mutableStateOf(true) }
    var isRecoverAccountChecked by rememberSaveable { mutableStateOf(true) }
    Column {
        Text(
            text = content,
            style = Typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 64.dp)
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(all = Spacing.fourX),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                24.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Button(onClick = {
                ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
                    walletConnectUri = walletConnectUri,
                )
            }) {
                Text(
                    text = stringResource(R.string.open_deeplink_popup),
                )
            }

            Button(onClick = {
                ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
                    walletConnectUri = walletConnectUri,
                )
            }) {
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
                    runCatching {
                        ConcordiumIDAppPopup.invokeIdAppActionsPopup(
                            walletConnectSessionTopic = walletConnectSessionTopic,
                            onCreateAccount = if (isCreateAccountChecked) {
                                {
                                    println("onCreate")
                                }
                            } else null,
                            onRecoverAccount = if (isRecoverAccountChecked) {
                                {
                                    println("onCreate")
                                }
                            } else null,
                        )
                    }.onFailure {
                        it.printStackTrace()
                        Toast.makeText(
                            context,
                            "at least one box must be checked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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