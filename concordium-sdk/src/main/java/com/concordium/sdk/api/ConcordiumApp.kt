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
import com.concordium.sdk.crypto.wallet.UnsignedCredentialInput
import com.concordium.sdk.crypto.wallet.credential.CredentialDeploymentDetails
import com.concordium.sdk.crypto.wallet.credential.CredentialDeploymentSerializationContext
import com.concordium.sdk.crypto.wallet.credential.UnsignedCredentialDeploymentInfoWithRandomness
import com.concordium.sdk.crypto.wallet.identityobject.IdentityObject
import com.concordium.sdk.requests.BlockQuery
import com.concordium.sdk.responses.accountinfo.credential.AttributeType
import com.concordium.sdk.transactions.CredentialDeploymentTransaction
import com.concordium.sdk.transactions.CredentialPublicKeys
import com.concordium.sdk.transactions.Expiry
import com.concordium.sdk.transactions.Index
import org.apache.commons.codec.binary.Hex
import org.json.JSONObject
import java.util.Collections
import java.util.EnumMap

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


    private fun createCredentialRequest(
        wallet: ConcordiumHdWallet,
        identity: IdentityObject? = null,
        ipIdentity: Int,
    ): UnsignedCredentialDeploymentInfoWithRandomness {
        val client = ClientService.client
        val anonymityRevokers =
            Iterable { client.getAnonymityRevokers(BlockQuery.BEST) }.associateBy { it.arIdentity.toString() }
        val providers = client.getIdentityProviders(BlockQuery.BEST)
        val provider = Iterable { providers }.find { it.ipIdentity.value == ipIdentity }
        val global = client.getCryptographicParameters(BlockQuery.BEST)

        val attributeRandomness: MutableMap<AttributeType, String> =
            EnumMap(AttributeType::class.java)
        for (attrType in AttributeType.entries) {
            attributeRandomness[attrType] =
                wallet.getAttributeCommitmentRandomness(
                    ipIdentity,
                    Constants.IDENTITY_INDEX,
                    Constants.CREDENTIAL_COUNTER,
                    attrType.ordinal
                )
        }

        val input: UnsignedCredentialInput = UnsignedCredentialInput.builder()
            .ipInfo(provider!!)
            .globalContext(global)
            .arsInfos(anonymityRevokers)
            .idObject(identity!!)
            .credNumber(Constants.CREDENTIAL_COUNTER)
            .attributeRandomness(attributeRandomness)
            .blindingRandomness(
                wallet.getSignatureBlindingRandomness(
                    ipIdentity,
                    Constants.IDENTITY_INDEX
                )
            )
            .credentialPublicKeys(
                CredentialPublicKeys.from(
                    Collections.singletonMap(
                        Index.from(0),
                        wallet.getAccountPublicKey(
                            ipIdentity,
                            Constants.IDENTITY_INDEX,
                            Constants.CREDENTIAL_COUNTER
                        )
                    ), 1
                )
            )
            .idCredSec(wallet.getIdCredSec(ipIdentity, Constants.IDENTITY_INDEX))
            .prfKey(wallet.getPrfKey(ipIdentity, Constants.IDENTITY_INDEX))
            .revealedAttributes(emptyList())
            .build()

        return Credential.createUnsignedCredential(input)
    }

//    private fun parseUnsignedCdi(unsignedCdiText: String): UnsignedCredentialDeploymentInfoWithRandomness {
//        return Credential.createUnsignedCredential(
//            UnsignedCredentialInput.builde
//        )
//    }

    fun signAndSubmit(
        seedPhrase: String,
        inputTranx: String,
        network: Network = Network.TESTNET
    ) {
//        checkForInitialization()

        // parse the transaction
        val json = JSONObject(inputTranx)
        val unsignedCdiText = json.getString(KEY_UNSIGNED_STR)
        val expiryInMs = json.getLong(KEY_EXPIRY)

        Logger.d("unsignedCdi: $unsignedCdiText")
        Logger.d("expiryInMs : $expiryInMs")

        val wallet: ConcordiumHdWallet =
            ConcordiumHdWallet.fromSeedPhrase(seedPhrase, network)

        val expiry = Expiry.from(expiryInMs).addMinutes(configuration.tranxExpiryInMins)

        // TODO check here what data shall be provided identity and ipIdentity
        val credentialDeploymentRequestInputWithRandomness = createCredentialRequest(
            wallet = wallet,
            identity = null,
            ipIdentity = 0
        )
        val unsignedCdi = credentialDeploymentRequestInputWithRandomness.unsignedCdi

        // generate signature
        val credentialDeploymentSignDigest = Credential.getCredentialDeploymentSignDigest(
            CredentialDeploymentDetails(unsignedCdi, expiry)
        )

        val accountSigningKey = wallet.getAccountSigningKey(
            0,
            0,
            0,
        )
        val signature: ByteArray = accountSigningKey.sign(credentialDeploymentSignDigest);
        Logger.d("accountSigningKey: $accountSigningKey")
        Logger.d("signature: $signature")

        // create payload to send to blockchain
        val credentialDeploymentTransaction = signCredentialTransaction(
            CredentialDeploymentDetails(unsignedCdi, expiry),
            signature,
        )

        // Send payload to blockchain
        submitCCDTransaction(credentialDeploymentTransaction)
    }

    fun signCredentialTransaction(
        credentialDeploymentDetails: CredentialDeploymentDetails,
        signature: ByteArray,
    ): CredentialDeploymentTransaction {
        val context = CredentialDeploymentSerializationContext(
            credentialDeploymentDetails.unsignedCdi,
            Collections.singletonMap(Index.from(0), Hex.encodeHexString(signature))
        )
        val credentialPayload = Credential.serializeCredentialDeploymentPayload(context)

        val credentialDeploymentTransaction =
            CredentialDeploymentTransaction.from(
                credentialDeploymentDetails.expiry,
                credentialPayload
            )
        return credentialDeploymentTransaction
    }

    fun submitCCDTransaction(credentialDeploymentTransaction: CredentialDeploymentTransaction) {
        // Send payload to client:
        val client = ClientService.client

        val responseHash =
            client.sendCredentialDeploymentTransaction(credentialDeploymentTransaction)
        Logger.d("responseHash $responseHash")
    }

    internal object ClientService {
        private val connection: Connection = Connection.newBuilder()
            .host(Constants.GRPC_URL)
            .port(Constants.GRPC_PORT)
            .useTLS(TLSConfig.auto())
            .build()

        val client: ClientV2 = ClientV2.from(connection)
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