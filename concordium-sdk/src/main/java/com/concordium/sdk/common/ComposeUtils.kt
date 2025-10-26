package com.concordium.sdk.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPixels() = with(LocalDensity.current) { this@toPixels.toPx() }

@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }