package com.concordium.sdk.api

import android.annotation.SuppressLint
import android.content.Context
import com.concordium.sdk.crypto.wallet.ConcordiumHdWallet
import com.concordium.sdk.crypto.wallet.Network

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

    fun signAndSubmit(seedPhrase: String) {
        val wallet: ConcordiumHdWallet =
            ConcordiumHdWallet.fromSeedPhrase(seedPhrase, Network.TESTNET)

        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            0
        );


//        Concord
    }

    fun clear() {
        _context = null
    }
}