package com.concordium.sdk.ui.model

sealed interface AccountAction {
    object Recover : AccountAction
    class Create(val code: String) : AccountAction
    class CreateOrRecover(val code: String) : AccountAction

    companion object {
        fun from(action: String?, code: String = ""): AccountAction {
            return when (action) {
                "create_or_recover" -> CreateOrRecover(code)
                "create" -> Create(code)
                else -> Recover
            }
        }
    }
}

enum class UserJourneyStep {
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

data class UiState(
    val accountAction: AccountAction,
    val journeyStep: UserJourneyStep,
    val walletConnectUri: String = "",
)