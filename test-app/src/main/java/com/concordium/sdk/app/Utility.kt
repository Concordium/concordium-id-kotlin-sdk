package com.concordium.sdk.app

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

internal fun readJsonFromAssets(context: Context, fileName: String): String {
    val stringBuilder = StringBuilder()
    try {
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        bufferedReader.useLines { lines ->
            lines.forEach { stringBuilder.append(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}