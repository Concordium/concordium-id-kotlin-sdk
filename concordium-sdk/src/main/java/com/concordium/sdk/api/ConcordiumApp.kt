package com.concordium.sdk.api

import android.annotation.SuppressLint
import android.content.Context
import com.concordium.sdk.crypto.wallet.ConcordiumHdWallet
import com.concordium.sdk.crypto.wallet.Credential
import com.concordium.sdk.crypto.wallet.Network
import com.concordium.sdk.crypto.wallet.credential.CredentialDeploymentDetails
import com.concordium.sdk.transactions.Expiry
import org.json.JSONObject


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

    fun signAndSubmit(seedPhrase: String, inputTranx: String) {
        val json = JSONObject(inputTranx)
        val unsignedCdi = json.getString(
            "unsignedCdiStr"
        )
        val expiryValue = json.getLong("expiry")

        val wallet: ConcordiumHdWallet =
            ConcordiumHdWallet.fromSeedPhrase(seedPhrase, Network.TESTNET)

        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            0
        )

        // Create digest of cred deployment tx
        val credentialDeploymentSignDigest: Credential(
            CredentialDeploymentDetails(
                unsignedCdi,
                Expiry.createNew().addSeconds(expiryValue)
            )
        )


        // Generate signature
        val signature = accountSigningKey.sign(credentialDeploymentSignDigest);


        println("accountSigningKey: $accountSigningKey")

        var expiryNew = Expiry.createNew().addMinutes(16)

    }



    fun clear() {
        _context = null
    }
}