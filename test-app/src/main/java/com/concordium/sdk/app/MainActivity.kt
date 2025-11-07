package com.concordium.sdk.app

import android.content.Context
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.concordium.idapp.sdk.api.ConcordiumIDAppPopup
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK
import com.concordium.idapp.sdk.app.R
import com.concordium.sdk.app.AppConstants.DUMMY_SEED_PHRASE
import com.concordium.sdk.app.AppConstants.TRANX_FILE_NAME
import com.concordium.sdk.app.ui.theme.ConcordiumIdAppSdkAppTheme
import com.concordium.sdk.app.ui.theme.Typography
import com.concordium.sdk.crypto.wallet.Network

private const val walletConnectUri =
    "wc:2b4e5df1-91e3-4c62-9d0a-dc2318a1f2d2@2?relay-protocol=irn&symKey=dcf9e8f542e24435b7d4a6785a1e8b32e2b03728f6b6a8a5c6e4d1b6b3a9d8cf"
private const val walletConnectSessionTopic = "dcf9e8f542e24435b7d4a6785a1e8b32e2b"

internal class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConcordiumIdAppSdkAppTheme {
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

const val PREFS_NAME = "ConcordiumIdAppPrefs"
const val SEED_PHRASE_KEY = "seed_phrase_key"

@Composable
fun ConcordiumScreen(
    content: String,
    callback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    var seedPhrase by rememberSaveable { mutableStateOf(sharedPreferences.getString(SEED_PHRASE_KEY, DUMMY_SEED_PHRASE) ?: DUMMY_SEED_PHRASE) }
    var isCreateAccountChecked by rememberSaveable { mutableStateOf(true) }
    var isRecoverAccountChecked by rememberSaveable { mutableStateOf(true) }
    var isMainnetNetwork by rememberSaveable { mutableStateOf(false) }
    val network = if (isMainnetNetwork) Network.MAINNET else Network.TESTNET

    LaunchedEffect(Unit) {
        callback()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = content,
            style = Typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SeedPhraseAndTransactionSection(
                seedPhrase = seedPhrase,
                onSeedPhraseChange = { seedPhrase = it },
                onSaveClick = {
                    sharedPreferences.edit(commit = true) { putString(SEED_PHRASE_KEY, seedPhrase) }
                    Toast.makeText(context, R.string.seed_phrase_saved, Toast.LENGTH_SHORT).show()
                },
                network = network,
                context = context
            )

            Spacer(Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(Modifier.height(32.dp))

            DeeplinkAndActionsSection(
                isCreateAccountChecked = isCreateAccountChecked,
                onIsCreateAccountCheckedChange = { isCreateAccountChecked = it },
                isRecoverAccountChecked = isRecoverAccountChecked,
                onIsRecoverAccountCheckedChange = { isRecoverAccountChecked = it },
                onInvokeDeeplinkClick = {
                    ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
                        walletConnectUri = walletConnectUri,
                    )
                },
                onInvokeActionsClick = {
                    runCatching {
                        ConcordiumIDAppPopup.invokeIdAppActionsPopup(
                            walletConnectSessionTopic = walletConnectSessionTopic,
                            onCreateAccount = if (isCreateAccountChecked) {
                                {
                                    println("onCreate Account")
                                }
                            } else null,
                            onRecoverAccount = if (isRecoverAccountChecked) {
                                {
                                    println("onRecover Account")
                                }
                            } else null,
                        )
                    }.onFailure {
                        it.printStackTrace()
                        Toast.makeText(
                            context,
                            R.string.at_least_one_box_must_be_checked,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.network_testnet))
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isMainnetNetwork,
                    onCheckedChange = { isMainnetNetwork = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.network_mainnet))
            }
        }
    }
}

@Composable
private fun SeedPhraseAndTransactionSection(
    seedPhrase: String,
    onSeedPhraseChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    network: Network,
    context: Context
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.seed_phrase_and_transaction_title), style = Typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = seedPhrase,
            onValueChange = onSeedPhraseChange,
            label = { Text(stringResource(id = R.string.seed_phrase_label)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onSaveClick) {
            Text(text = stringResource(id = R.string.save_seed_phrase))
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            ConcordiumIDAppSDK.signAndSubmit(
                seedPhrase = seedPhrase,
                serializedCredentialDeploymentTransaction = readJsonFromAssets(
                    context = context,
                    fileName = TRANX_FILE_NAME,
                ),
                network = network,
            )
        }) {
            Text(
                text = stringResource(R.string.sign_submit_traxn),
            )
        }
    }
}

@Composable
private fun DeeplinkAndActionsSection(
    isCreateAccountChecked: Boolean,
    onIsCreateAccountCheckedChange: (Boolean) -> Unit,
    isRecoverAccountChecked: Boolean,
    onIsRecoverAccountCheckedChange: (Boolean) -> Unit,
    onInvokeDeeplinkClick: () -> Unit,
    onInvokeActionsClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(id = R.string.deeplink_and_actions_title), style = Typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onInvokeDeeplinkClick) {
            Text(
                text = stringResource(R.string.open_deeplink_popup),
            )
        }
        Spacer(Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCreateAccountChecked,
                onCheckedChange = onIsCreateAccountCheckedChange
            )
            Text(text = stringResource(id = R.string.create_account_label))
            Spacer(Modifier.width(16.dp))
            Checkbox(
                checked = isRecoverAccountChecked,
                onCheckedChange = onIsRecoverAccountCheckedChange
            )
            Text(text = stringResource(id = R.string.recover_account_label))
        }
        Button(onClick = onInvokeActionsClick) {
            Text(
                text = stringResource(R.string.open_actions_popup),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConcordiumIdAppSdkAppTheme {
        ConcordiumScreen(
            stringResource(R.string.app_name),
            callback = {}
        )
    }
}
