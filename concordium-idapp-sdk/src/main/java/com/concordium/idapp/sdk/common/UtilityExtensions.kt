package com.concordium.idapp.sdk.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri

private val WC_URI_REGEX = Regex("""^wc:[0-9a-fA-F-]+@\d+""")

internal fun String.isValiWalletConnectUri(): Boolean {
    val beforeQuestion = this.substringBefore("?")
    return WC_URI_REGEX.matches(beforeQuestion)
}

internal fun String.isValidWalletConnectSessionTopic() : Boolean {
    return this.length > 4
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
internal fun Dp.toPixels() = with(LocalDensity.current) { this@toPixels.toPx() }

@Composable
internal fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
internal fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }