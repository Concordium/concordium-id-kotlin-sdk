package com.concordium.sdk.api

import android.annotation.SuppressLint
import android.content.Context
import com.concordium.sdk.ClientV2
import com.concordium.sdk.Connection
import com.concordium.sdk.TLSConfig
import com.concordium.sdk.api.Configuration.Companion.PROD
import com.concordium.sdk.api.model.CCDAccountKeyPair
import com.concordium.sdk.common.Constants
import com.concordium.sdk.common.Logger
import com.concordium.sdk.common.isValidateMnemonic
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
    const val KEY_UNSIGNED_STR = "unsignedCdiStr"
    const val KEY_EXPIRY = "expiry"

    internal lateinit var configuration: Configuration

    private var _context: Context? = null
    val context: Context
        get() = _context!!

    fun initialize(
        context: Context,
        configuration: Configuration = PROD
    ) {
        this._context = context
        this.configuration = configuration
    }

    fun generateAccountWithSeedPhrase(
        seed: String,
        network: Network,
        accountIndex: Int = 0,
        credentialCounter: Int = 0
    ): CCDAccountKeyPair {
        // below check is already inside sdk
        require(seed.isValidateMnemonic()) {
            "seed shall be mnemonic phrase"
        }

        val wallet: ConcordiumHdWallet =
            ConcordiumHdWallet.fromSeedPhrase(seed, network)
        val publicKey = wallet.getAccountPublicKey(0, 0, credentialCounter)
        val signingKey = wallet.getAccountSigningKey(0, 0, credentialCounter)

        return object : CCDAccountKeyPair {
            override val publicKey = publicKey.toString()
            override val signingKey = signingKey.toString()
        }
    }

    fun getCreateAccountCreationRequest(
        publicKey: String,
        reason: String,
    ) {

    }

    fun signAndSubmit(
        seedPhrase: String,
        inputTranx: String,
        accountIndex: Int = 0,
        network: Network = Network.MAINNET,
    ): String {
        // parse the transaction
        val json = JSONObject(inputTranx)
        val unsignedCdiText = json.getString(KEY_UNSIGNED_STR)
        val expiryInMs = json.getLong(KEY_EXPIRY)

        val unsignedCdi: UnsignedCredentialDeploymentInfo = JsonMapper.INSTANCE.readValue(
            unsignedCdiText,
            UnsignedCredentialDeploymentInfo::class.java
        )

        Logger.d("unsignedCdi: $unsignedCdiText")
        Logger.d("expiryInMs : $expiryInMs")

        val wallet: ConcordiumHdWallet = ConcordiumHdWallet.fromSeedPhrase(seedPhrase, network)
        val expiry = Expiry.from(expiryInMs)

        // generate signature
        val credentialDeploymentSignDigest = Credential.getCredentialDeploymentSignDigest(
            CredentialDeploymentDetails(unsignedCdi, expiry)
        )

        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            accountIndex,
        )
        val signature: ByteArray = accountSigningKey.sign(credentialDeploymentSignDigest)

        Logger.d("accountSigningKey: $accountSigningKey")
        Logger.d("signature: $signature")

        // create payload tranx to send to blockchain
        val credentialDeploymentTransaction = createCredentialTransactionPayload(
            CredentialDeploymentDetails(unsignedCdi, expiry),
            signature,
        )

        // Send payload to blockchain
        val response = submitCCDTransaction(network, credentialDeploymentTransaction)
        return response
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


    /**
     *
     */
    fun submitCCDTransaction(
        network: Network,
        credentialDeploymentTransaction: CredentialDeploymentTransaction
    ): String {
        val client = ClientService.getClient(network = network)

        val responseHash =
            client.sendCredentialDeploymentTransaction(credentialDeploymentTransaction)
        Logger.d("responseHash $responseHash")
        return responseHash.toString()
    }

    internal object ClientService {
        fun getClient(network: Network): ClientV2 {
            val url =
                if (network == Network.TESTNET) Constants.GRPC_TEST_URL else Constants.GRPC_MAINNET_URL
            val connection: Connection = Connection.newBuilder()
                .host(url)
                .port(Constants.GRPC_PORT)
                .useTLS(TLSConfig.auto())
                .build()

            return ClientV2.from(connection)
        }
    }


    internal fun checkForInitialization() {
        require(_context != null) {
            "ConcordiumIDAppSDK not initialized"
        }
    }


    fun clear() {
        _context = null
    }
}