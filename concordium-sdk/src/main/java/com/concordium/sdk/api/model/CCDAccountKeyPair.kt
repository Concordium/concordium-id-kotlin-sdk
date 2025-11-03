package com.concordium.sdk.api.model

interface CCDAccountKeyPair {
    val publicKey: String
    val signingKey: String
}