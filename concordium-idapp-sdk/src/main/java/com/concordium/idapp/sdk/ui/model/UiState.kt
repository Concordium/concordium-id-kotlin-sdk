package com.concordium.idapp.sdk.ui.model


internal enum class UserJourneyStep {
    Connect,
    IdVerification,
    Account;

    companion object {
        fun from(value: String?): UserJourneyStep {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: Connect
        }
    }
}

internal data class UiState(
    val journeyStep: UserJourneyStep,
    val codeText: String = "",
    val walletConnectUri: String = "",
)