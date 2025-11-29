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
import java.util.Collections

@SuppressLint("StaticFieldLeak")
object ConcordiumIDAppSDK {
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
     * @param seedPhrase seed phrase for the wallet
     * @param expiry expiry time in seconds since unix epoch
     * @param unsignedCdiStr unsigned credential deployment info string
     * @param accountIndex account index, defaults to 0
     * @param network, defaults to Mainnet
     */
    fun signAndSubmit(
        seedPhrase: String,
        expiry: Long,
        unsignedCdiStr: String,
        accountIndex: Int = 0,
        network: Network = Network.MAINNET,
    ): String {
        require(seedPhrase.isNotBlank()) { "Seed phrase cannot be empty" }
        require(unsignedCdiStr.isNotBlank()) { "Unsigned CDI string cannot be empty" }
        require(accountIndex >= 0) { "Account index must be non-negative" }
        require(expiry > 0) { "Expiry must be positive" }
        // parse the transaction
        val unsignedCdi: UnsignedCredentialDeploymentInfo
        try {
            unsignedCdi = JsonMapper.INSTANCE.readValue(
                unsignedCdiStr,
                UnsignedCredentialDeploymentInfo::class.java
            )
        } catch (e: Exception) {
            Logger.e("Error parsing serialized CredentialDeploymentTransaction: $e")
            throw IllegalArgumentException("Invalid credential deployment info format", e)
        }

        Logger.d("sign and submit transaction")

        // generate signature
        val expiryTimestamp = Expiry.from(expiry)
        val wallet = ConcordiumHdWallet.fromSeedPhrase(seedPhrase, network)
        val credentialDeploymentDetails = CredentialDeploymentDetails(unsignedCdi, expiryTimestamp)
        val credentialDeploymentSignDigest =
            Credential.getCredentialDeploymentSignDigest(credentialDeploymentDetails)
        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            accountIndex,
        )
        val signature = accountSigningKey.sign(credentialDeploymentSignDigest)

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

    /**
     * create payload tranx to send to blockchain
     * @param credentialDeploymentDetails
     * @param signature
     */
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

    /**
    * Send payload to blockchain
    * @param network
    * @param credentialDeploymentTransaction
    */
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