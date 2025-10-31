//package com.concordium.sdk.app.model
//
//import com.google.gson.annotations.SerializedName
//
///**
// * POJO classes that map the structure of the `tranx.json` attachment.
// * These are Kotlin data classes suitable for Gson serialization/deserialization.
// */
//data class Tranx(
//    @SerializedName("expiry")
//    val expiry: Long,
//
//    @SerializedName("randomness")
//    val randomness: Randomness,
//
//    /**
//     * This field contains an escaped JSON string in the sample file. Keep as String
//     * unless you want to parse it into a nested object separately.
//     */
//    @SerializedName("unsignedCdiStr")
//    val unsignedCdiStr: String,
//
//    @SerializedName("revocationThreshold")
//    val revocationThreshold: Int
//)
//
//data class Randomness(
//    @SerializedName("attributesRand")
//    val attributesRand: AttributesRand,
//
//    @SerializedName("credCounterRand")
//    val credCounterRand: String,
//
//    @SerializedName("idCredSecRand")
//    val idCredSecRand: String,
//
//    @SerializedName("maxAccountsRand")
//    val maxAccountsRand: String,
//
//    @SerializedName("prfRand")
//    val prfRand: String
//)
//
//data class AttributesRand(
//    @SerializedName("countryOfResidence")
//    val countryOfResidence: String,
//
//    @SerializedName("dob")
//    val dob: String,
//
//    @SerializedName("firstName")
//    val firstName: String,
//
//    @SerializedName("idDocExpiresAt")
//    val idDocExpiresAt: String,
//
//    @SerializedName("idDocIssuedAt")
//    val idDocIssuedAt: String,
//
//    @SerializedName("idDocIssuer")
//    val idDocIssuer: String,
//
//    @SerializedName("idDocNo")
//    val idDocNo: String,
//
//    @SerializedName("idDocType")
//    val idDocType: String,
//
//    @SerializedName("lastName")
//    val lastName: String,
//
//    @SerializedName("nationalIdNo")
//    val nationalIdNo: String,
//
//    @SerializedName("nationality")
//    val nationality: String,
//
//    @SerializedName("sex")
//    val sex: String,
//
//    @SerializedName("taxIdNo")
//    val taxIdNo: String
//)
