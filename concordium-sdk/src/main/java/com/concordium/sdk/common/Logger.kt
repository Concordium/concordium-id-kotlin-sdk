package com.concordium.sdk.common

import android.util.Log
import com.concordium.sdk.api.ConcordiumIDAppSDK

object Logger {
    private const val TAG = "Concord-SDK"
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