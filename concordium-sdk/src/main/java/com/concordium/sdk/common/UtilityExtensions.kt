package com.concordium.sdk.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri

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