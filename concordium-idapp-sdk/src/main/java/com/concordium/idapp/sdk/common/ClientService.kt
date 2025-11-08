package com.concordium.idapp.sdk.common

import com.concordium.sdk.ClientV2
import com.concordium.sdk.Connection
import com.concordium.sdk.TLSConfig
import com.concordium.sdk.crypto.wallet.Network

internal object ClientService {
    private var client: ClientV2? = null
    fun getClient(network: Network): ClientV2 {
        val configuration: Configuration =
            if (network == Network.TESTNET) Testnet else Mainnet

        val connection: Connection = Connection.newBuilder()
            .host(configuration.grpcUrl)
            .port(configuration.grpcPort)
            .useTLS(TLSConfig.auto())
            .build()
        client = ClientV2.from(connection)
        return client!!
    }

    fun close() {
        client?.close()
    }
}