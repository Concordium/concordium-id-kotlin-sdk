package com.concordium.idapp.sdk.api

import android.annotation.SuppressLint
import android.content.Context
import com.concordium.idapp.sdk.api.model.CCDAccountKeyPair
import com.concordium.idapp.sdk.common.ClientService
import com.concordium.idapp.sdk.common.Logger
import com.concordium.sdk.crypto.wallet.ConcordiumHdWallet
import com.concordium.sdk.crypto.wallet.Credential
import com.concordium.sdk.crypto.wallet.Network
import com.concordium.sdk.crypto.wallet.credential.CredentialDeploymentDetails
import com.concordium.sdk.crypto.wallet.credential.CredentialDeploymentSerializationContext
import com.concordium.sdk.crypto.wallet.credential.UnsignedCredentialDeploymentInfo
import com.concordium.sdk.serializing.JsonMapper
import com.concordium.sdk.transactions.CredentialDeploymentTransaction
import com.concordium.sdk.transactions.Expiry
import com.concordium.sdk.transactions.Index
import org.apache.commons.codec.binary.Hex
import org.json.JSONObject
import java.util.Collections

@SuppressLint("StaticFieldLeak")
object ConcordiumIDAppSDK {
    internal const val KEY_UNSIGNED_STR = "unsignedCdiStr"
    internal const val KEY_EXPIRY = "expiry"
    internal var enableDebugging: Boolean = false

    private var _context: Context? = null
    internal val context: Context
        get() = _context!!

    fun initialize(
        context: Context,
        enableDebugLog: Boolean = false,
    ) {
        this._context = context
        this.enableDebugging = enableDebugLog
    }

    /**
     * sign and submit transaction to blockchain
     * @param seedPhrase
     * @param serializedCredentialDeploymentTransaction
     * @param accountIndex
     * @param network, defaults to Mainnet
     */
    fun signAndSubmit(
        seedPhrase: String,
        serializedCredentialDeploymentTransaction: String,
        accountIndex: Int = 0,
        network: Network = Network.MAINNET,
    ): String {
        Logger.d("sign and submit tranx")
        // parse the transaction
        val json = JSONObject(serializedCredentialDeploymentTransaction)
        val unsignedCdiText = json.getString(KEY_UNSIGNED_STR)
        val expiryInMs = json.getLong(KEY_EXPIRY)

        val unsignedCdi = JsonMapper.INSTANCE.readValue(
            unsignedCdiText,
            UnsignedCredentialDeploymentInfo::class.java
        )
        Logger.d("unsignedCdi: $unsignedCdiText")
        Logger.d("expiryInMs : $expiryInMs")

        // generate signature
        val expiry = Expiry.from(expiryInMs)
        val wallet = ConcordiumHdWallet.fromSeedPhrase(seedPhrase, network)
        val credentialDeploymentDetails = CredentialDeploymentDetails(unsignedCdi, expiry)
        val credentialDeploymentSignDigest =
            Credential.getCredentialDeploymentSignDigest(credentialDeploymentDetails)
        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            accountIndex,
        )
        val signature = accountSigningKey.sign(credentialDeploymentSignDigest)
        Logger.d("accountSigningKey: $accountSigningKey")
        Logger.d("signature: $signature")

        // create payload tranx to send to blockchain
        val credentialDeploymentTransaction = createCredentialTransactionPayload(
            credentialDeploymentDetails,
            signature,
        )

        // Send payload to blockchain
        val response = submitCCDTransaction(network, credentialDeploymentTransaction)
        Logger.d("transactionHash = $response")
        return response
    }

    /**
     * generate account key pair from seed phrase
     * @param seed
     * @param network, defaults to Mainnet
     * @param accountIndex
     */
    fun generateAccountWithSeedPhrase(
        seed: String,
        network: Network,
        accountIndex: Int = 0,
    ): CCDAccountKeyPair {
        val wallet: ConcordiumHdWallet =
            ConcordiumHdWallet.fromSeedPhrase(seed, network)
        val publicKey = wallet.getAccountPublicKey(0, 0, accountIndex)
        val signingKey = wallet.getAccountSigningKey(0, 0, accountIndex)

        return object : CCDAccountKeyPair {
            override val publicKey = publicKey.toString()
            override val signingKey = signingKey.toString()
        }
    }

    /**
     * cleaning up resources
     */
    fun clear() {
        _context = null
        ClientService.close()
        Logger.d("ConcordiumIDAppSDK cleared")
    }

    internal fun checkForInitialization() {
        require(_context != null) {
            "ConcordiumIDAppSDK not initialized"
        }
    }

    private fun createCredentialTransactionPayload(
        credentialDeploymentDetails: CredentialDeploymentDetails,
        signature: ByteArray,
    ): CredentialDeploymentTransaction {
        val context = CredentialDeploymentSerializationContext(
            credentialDeploymentDetails.unsignedCdi,
            Collections.singletonMap(Index.from(0), Hex.encodeHexString(signature))
        )
        val credentialPayload = Credential.serializeCredentialDeploymentPayload(context)

        val credentialDeploymentTransactionPayload =
            CredentialDeploymentTransaction.from(
                credentialDeploymentDetails.expiry,
                credentialPayload
            )
        return credentialDeploymentTransactionPayload
    }

    private fun submitCCDTransaction(
        network: Network,
        credentialDeploymentTransaction: CredentialDeploymentTransaction
    ): String {
        val client = ClientService.getClient(network = network)

        val transactionHash =
            client.sendCredentialDeploymentTransaction(credentialDeploymentTransaction)
        return transactionHash.toString()
    }
}