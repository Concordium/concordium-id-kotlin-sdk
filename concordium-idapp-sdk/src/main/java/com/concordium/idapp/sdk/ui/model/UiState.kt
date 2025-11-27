package com.concordium.idapp.sdk.ui.model

internal sealed interface AccountAction {
    class Create(val code: String) : AccountAction

    companion object {
        fun from(action: String?, code: String = ""): AccountAction {
            return when (action) {
                "create" -> Create(code)
                else -> throw IllegalStateException("Unsupported account action: $action")
            }
        }
    }
}

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
    val accountAction: AccountAction,
    val journeyStep: UserJourneyStep,
    val walletConnectUri: String = "",
)