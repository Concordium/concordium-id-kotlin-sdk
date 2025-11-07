package com.concordium.idapp.sdk.common

import android.util.Log
import com.concordium.idapp.sdk.api.ConcordiumIDAppSDK

internal object Logger {
    private const val TAG = "Concordium-IDApp-SDK"
    private val isDebug = ConcordiumIDAppSDK.enableDebugging

    fun d(message: String) {
        if (isDebug) {
            Log.d(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isDebug) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }

    fun i(message: String) {
        if (isDebug) {
            Log.i(TAG, message)
        }
    }

    fun w(message: String) {
        if (isDebug) {
            Log.w(TAG, message)
        }
    }
}