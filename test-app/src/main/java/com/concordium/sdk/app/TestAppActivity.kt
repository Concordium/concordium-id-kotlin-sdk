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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.concordium.idapp.sdk.app.BuildConfig
import com.concordium.idapp.sdk.app.R
import com.concordium.sdk.app.AppConstants.DUMMY_PUBLIC_KEY
import com.concordium.sdk.app.AppConstants.DUMMY_SEED_PHRASE
import com.concordium.sdk.app.AppConstants.KEY_PUBLIC_KEY
import com.concordium.sdk.app.AppConstants.PREFS_NAME
import com.concordium.sdk.app.AppConstants.SEED_PHRASE_KEY
import com.concordium.sdk.app.ui.theme.ConcordiumIdAppSdkAppTheme
import com.concordium.sdk.app.ui.theme.Typography
import com.concordium.sdk.crypto.wallet.Network
import kotlinx.coroutines.launch

internal class TestAppActivity : ComponentActivity() {
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

@Composable
fun ConcordiumScreen(
    content: String,
    callback: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMainnetNetwork by rememberSaveable { mutableStateOf(false) }
    val network = if (isMainnetNetwork) Network.MAINNET else Network.TESTNET

    LaunchedEffect(Unit) {
        callback()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = content,
                style = Typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
            )
        }
        item {
            NetworkSwitchContainer(
                modifier = Modifier.padding(top = 16.dp),
                isMainnetNetwork = isMainnetNetwork,
                onIsMainnetNetworkChange = { isMainnetNetwork = it }
            )
        }
        item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }
        item { SeedPhraseAndTransactionSection(network = network) }
        item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }
        item { DeeplinkAndActionsSection() }
        item { HorizontalDivider(Modifier.padding(vertical = 16.dp)) }
        item { KeyAccountsSection(network = network) }
    }
}

@Composable
private fun KeyAccountsSection(network: Network, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var publicKey by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(KEY_PUBLIC_KEY, DUMMY_PUBLIC_KEY) ?: DUMMY_PUBLIC_KEY
        )
    }
    val scope = rememberCoroutineScope()

    Column(modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.get_key_accounts),
            style = Typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        EditableInputContainer(
            value = publicKey,
            prefKey = KEY_PUBLIC_KEY,
            label = stringResource(id = R.string.public_key),
            onUpdateCallback = {
                publicKey = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = {
            scope.launch {
                val keyAccounts = ConcordiumIDAppSDK.getKeyAccounts(
                    publicKey = publicKey,
                    network = network,
                )
                println("keyAccounts: $keyAccounts")
            }
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = stringResource(R.string.get_key_accounts),
            )
        }
    }
}

@Composable
private fun NetworkSwitchContainer(
    isMainnetNetwork: Boolean,
    onIsMainnetNetworkChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(id = R.string.network_testnet))
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isMainnetNetwork,
            onCheckedChange = onIsMainnetNetworkChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = R.string.network_mainnet))
    }
}

@Composable
private fun SeedPhraseAndTransactionSection(
    network: Network,
) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var seedPhrase by rememberSaveable {
        mutableStateOf(
            sharedPreferences.getString(SEED_PHRASE_KEY, DUMMY_SEED_PHRASE) ?: DUMMY_SEED_PHRASE
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.seed_phrase_and_transaction_title),
            style = Typography.titleMedium
        )
        Spacer(Modifier.height(16.dp))
        EditableInputContainer(
            value = seedPhrase,
            prefKey = SEED_PHRASE_KEY,
            label = stringResource(id = R.string.seed_phrase_label),
            onUpdateCallback = { seedPhrase = it }
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            runCatching {
                ConcordiumIDAppSDK.signAndSubmit(
                    seedPhrase = seedPhrase,
                    expiry = BuildConfig.EXPIRY,
                    unsignedCdiStr = BuildConfig.UNSIGNED_CDI_STRING,
                    network = network,
                )
            }.onFailure {
                it.printStackTrace()
                Toast.makeText(
                    context,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }.onSuccess {
                Toast.makeText(
                    context,
                    R.string.sign_submit_success,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(
                text = stringResource(R.string.sign_submit_traxn),
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val response = ConcordiumIDAppSDK.generateAccountWithSeedPhrase(
                seedPhrase,
                network = network,
                accountIndex = 0,
            )
            println("response: ${response.publicKey} ${response.signingKey}")
        }) {
            Text(
                text = stringResource(R.string.generate_key_pair),
            )
        }


    }
}

@Composable
private fun DeeplinkAndActionsSection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.deeplink_and_actions_title),
            style = Typography.titleMedium
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            ConcordiumIDAppPopup.invokeIdAppDeepLinkPopup(
                walletConnectUri = BuildConfig.WC_URI,
            )
        }) {
            Text(
                text = stringResource(R.string.open_deeplink_popup),
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = {
            runCatching {
                ConcordiumIDAppPopup.invokeIdAppActionsPopup(
                    walletConnectSessionTopic = BuildConfig.WC_SESSION_TOPIC,
                    onCreateAccount = { println("onCreate Account") },
                )
            }.onFailure {
                it.printStackTrace()
                Toast.makeText(
                    context,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(
                text = stringResource(R.string.open_create_actions_popup),
            )
        }
    }
}

@Composable
private fun EditableInputContainer(
    prefKey: String,
    label: String,
    value: String,
    onUpdateCallback: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var currText by rememberSaveable(value) {
        mutableStateOf(value)
    }
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
    ) {
        OutlinedTextField(
            value = currText,
            onValueChange = { currText = it },
            label = { Text(text = label) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            sharedPreferences.edit(commit = true) { putString(prefKey, currText) }
            onUpdateCallback.invoke(currText)
            Toast.makeText(context, "$label saved successfully", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = stringResource(id = R.string.save))
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
