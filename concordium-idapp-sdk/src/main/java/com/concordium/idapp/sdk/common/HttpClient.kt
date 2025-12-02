package com.concordium.idapp.sdk.common

import com.concordium.sdk.crypto.wallet.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal object HttpClient {

    /**
     * Performs an HTTP GET request to the specified URL and returns the response body as a string.
     * @param url the URL to send the GET request to
     * @return the response body as a string
     * @throws IOException if an I/O error occurs or if the response code is not HTTP_OK
     */
    suspend fun get(url: String): String {
        return withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpsURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 20_000
                connection.readTimeout = 20_000
                connection.setRequestProperty("Accept", "application/json")

                val responseCode = connection.responseCode
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    val errorMessage =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                            ?: "HTTP Error $responseCode"
                    throw IOException("HTTP request failed with code $responseCode: $errorMessage")
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    /**
     * Builds the wallet-proxy URL for the given network
     * @param network the Concordium network
     * @return the base URL for the wallet-proxy service
     */
    fun getWalletProxyBaseUrl(network: Network): String {
        return when (network) {
            Network.MAINNET -> "https://wallet-proxy.mainnet.concordium.com"
            Network.TESTNET -> "https://wallet-proxy.testnet.concordium.com"
        }
    }
}