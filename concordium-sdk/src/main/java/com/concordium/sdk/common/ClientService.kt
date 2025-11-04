package com.concordium.sdk.common

import com.concordium.sdk.ClientV2
import com.concordium.sdk.Connection
import com.concordium.sdk.TLSConfig
import com.concordium.sdk.crypto.wallet.Network

internal object ClientService {

    private var client: ClientV2? = null
    fun getClient(network: Network): ClientV2 {
        val host =
            if (network == Network.TESTNET) Constants.GRPC_TEST_URL else Constants.GRPC_MAINNET_URL

        val connection: Connection = Connection.newBuilder()
            .host(host)
            .port(Constants.GRPC_PORT)
            .useTLS(TLSConfig.auto())
            .build()
        client = ClientV2.from(connection)
        return client!!
    }
    
    fun close() {
        client?.close()
    }
}