package com.concordium.idapp.sdk.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri

private val SESSION_TOPIC_REGEX = Regex("^[0-9a-f]{64}$")

private val WC_URI_REGEX = Regex(
    """^wc:[0-9a-fA-F-]+@[12]\?bridge=.+&key=[0-9a-fA-F]+$"""
)

fun String.isValidWalletConnectUri(): Boolean {
    val walletConnectUriPattern = """^wc:[a-fA-F0-9]{64}@2\?relay-protocol=[a-zA-Z]+&symKey=[a-fA-F0-9]{64}$""".toRegex()
    return walletConnectUriPattern.matches(this)
}
internal fun handleAppDeepLink(
    deeplink: String,
    context: Context,
    playStoreLink: String = Constants.ID_APP_PLAY_STORE_LINK,
): Boolean {
    val uri = deeplink.toUri()
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val packageManager = context.packageManager
    val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

    return if (activities.isNotEmpty()) {
        context.startActivity(intent)
        true
    } else {
        val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreLink.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(playStoreIntent)
        false
    }
}

@Composable
fun Dp.toPixels() = with(LocalDensity.current) { this@toPixels.toPx() }

@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }