//package com.concordium.sdk.api
//
//import com.concordium.sdk.ClientV2
//import com.concordium.sdk.TLSConfig
//import com.concordium.sdk.crypto.wallet.ConcordiumHdWallet
//import com.concordium.sdk.transactions.CredentialDeploymentTransaction
//import com.concordium.sdk.transactions.Index
//import java.util.Collections
//
//```
//
///// Input from idApp
//
//
//
//function signAndSubmit(seed, serializedCredentialDeploymentTransaction) -> txHash{
//    /// ----- IDAPP SDK ----
//
//    //////  ---- Parse the input ---
//    /// deserialise unsignedCdiStr
//    val unsignedCdi = JSONbig.parse(
//        serializedCredentialDeploymentTransaction.unsignedCdiStr,
//    );
//
//    /// fix the
//    val expiry =serializedCredentialDeploymentTransaction.expiry  //Expiry.createNew().addMinutes(transactionExpirationMinutes);
//
//    ///
//    val randomness = serializedCredentialDeploymentTransaction.randomness
//    //////  ---- Parse the input ---
//
//
//    //// Sign Transaction
//    val seedPhraseA = "throw action salad convince north kit zero rude mango whip dinner situate remove maple oval draw diesel envelope inmate laptop hill visa magic stand"
//
//    // Create wallet
//    ConcordiumHdWallet wallet = ConcordiumHdWallet.fromSeedPhrase(seedPhrase, Network.MAINNET);
//    // privatekey
//    val accountSigningKey = wallet.getAccountSigningKey(0,0,0);
//
//    // Create digest of cred deployment tx
//    val credentialDeploymentSignDigest = Credential.getCredentialDeploymentSignDigest(
//        new CredentialDeploymentDetails(
//                unsignedCdi,
//        expiry
//    )
//    );
//    // Generate signature
//    val signature = accountSigningKey.sign(credentialDeploymentSignDigest);
//
//
//    ///// Submit Tranaction on blockchain
//    // Create GRPC client
//    Connection.ConnectionBuilder connection = Connection.newBuilder()
//        .host("grpc.testnet.concordium.com")
//        .port(20000)
//        .useTLS(TLSConfig.auto());
//    ClientV2 client = ClientV2.from(connection.build());
//
//    // Payload
//    val context = new CredentialDeploymentSerializationContext(
//            unsignedCdi,
//    Collections.singletonMap(Index.from(0), Hex.encodeHexString(signature))
//    );
//    val credentialDeploymentPayload = Credential.serializeCredentialDeploymentPayload(context);
//
//    // Submit tx on chain using grpc client
//    client.sendCredentialDeploymentTransaction(
//        CredentialDeploymentTransaction.from(
//        expiry,
//        credentialDeploymentPayload
//    ));
//    // txHash
//}
//
//```