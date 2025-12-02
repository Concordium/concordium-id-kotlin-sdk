package com.concordium.idapp.sdk.api.model;

/**
 * Represents the public key information of a Key Account.
 */
interface KeyAccountPublicKey {
    val schemeId: String
    val verifyKey: String
}

/**
 * Represents a key Account with its associated details.
 */
interface KeyAccount {
    val address: String
    val credentialIndex: Number
    val isSimpleAccount: Boolean
    val keyIndex: Number
    val publicKey: KeyAccountPublicKey
}

/**
 * Implementation of KeyAccountPublicKey for JSON parsing
 */
internal data class KeyAccountPublicKeyImpl(
    override val schemeId: String,
    override val verifyKey: String
) : KeyAccountPublicKey

/**
 * Implementation of KeyAccount for JSON parsing
 */
internal data class KeyAccountImpl(
    override val address: String,
    override val credentialIndex: Number,
    override val isSimpleAccount: Boolean,
    override val keyIndex: Number,
    override val publicKey: KeyAccountPublicKey
) : KeyAccount