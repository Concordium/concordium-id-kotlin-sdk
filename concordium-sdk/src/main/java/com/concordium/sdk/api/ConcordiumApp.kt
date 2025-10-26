package com.concordium.sdk.api

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ConcordiumIDAppSDK {
    private var _context: Context? = null
    val context: Context
        get() = _context!!

    internal var enableDebugging = false

    fun initialize(context: Context, enableDebugging: Boolean = false) {
        this._context = context
        this.enableDebugging = enableDebugging
    }

    fun clear() {
        _context = null
    }
}