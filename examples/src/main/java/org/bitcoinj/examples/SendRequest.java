/*
 * Copyright by the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.examples;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MultiChainParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.signers.LocalTransactionSigner;
import org.bitcoinj.signers.TransactionSigner;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.KeyBag;
import org.bitcoinj.wallet.RedeemData;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.bitcoinj.core.ECKey;
import org.spongycastle.util.encoders.Hex;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Base64;

import java.io.File;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The following example shows you how to create a SendRequest to send coins from a wallet to a given address.
 */
public class SendRequest {
    private static Address forwardingAddress;
    private static WalletAppKit kit;

    public static void main(String[] args) throws Exception {

        args = new String[]{"17UGUPmLNtPRzT7DUHvWmQyLHcoKDUoH99DeEm"};

        // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
        BriefLogFormatter.init();
        if (args.length < 1) {
            System.err.println("Usage: address-to-send-back-to [regtest|testnet]");
            return;
        }

        // Figure out which network we should connect to. Each one gets its own set of files.

        final NetworkParameters params = MultiChainParams.get(
                "00ea493df401cee6694c68a35d2b50dbdd197bd630cc2a95875933e16b7d0590",
                "010000000000000000000000000000000000000000000000000000000000000000000000b59757b81c569f8b3854d83fe1b09b9a69a7b6ea1c33863a2a1640ee865ce1dc0f373059ffff0020a70100000101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff1704ffff002001040f4d756c7469436861696e20766f7465ffffffff0200000000000000002f76a914a18876083c6b7e3a76b0549dcd49cadf7222a05788ac1473706b703731000000000000ffffffff0f373059750000000000000000131073706b6e0200040101000104726f6f74756a00000000"
        );

        String filePrefix = "voting-wallet";

        // Parse the address given as the first parameter.
//        forwardingAddress = Address.fromBase58(params, args[0]);

        // Start up a basic app using a class that automates some boilerplate.
        kit = new WalletAppKit(params, new File("."), filePrefix);

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        PeerAddress peer = new PeerAddress(params, InetAddress.getByName("188.226.149.56"));


        kit.setPeerNodes(peer);

        //  Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();

//        daan@daan-XPS13-9333:~/Workspace/digital-voting-pass-util$ python create_keypair.py
//        Address: 14cdEY69ZCVCTtgebDPyqTWkAoS7SUm8fVdboh
//        Public: 1245d042e6ceb528e8185dbd93c558193d415aef2045f894685ae097a29f65e9ea9c751fda5d6dd7cc130e79caae4fe3116850b11787fbae468ec34124105c2a194ee950b26a83f2dcb310d0a17bb6fe
//        Private: af484d3743489f94de599b14d41eac656bae9a08a3a64c129104385717c861c53ce664c05038efd6
//        daan@daan-XPS13-9333:~/Workspace/digital-voting-pass-util$ python create_keypair.py
//        Address: 1iRbvVenrmw1SjHPTL1AviuRTJUac9iwi4HEv
//        Public: 6c79ceeb43b2b4d36bcdb82a6be2221798b292b109e597c7ec94ef7bf3aa2995310a9853642c768e03558a4d823e5ac31d262790c567917fcd902b9b748869e1d6dc169ed46fe2afa3ddcabcdac0ab59
//        Private: 313c37fb421297b9f81d3cd59a227ad9b2c3ceb1cedc4c30dfcf126f227dfa76766ffa04eca290ad


        ECKey passportKey = ECKey.fromPrivate(new BigInteger("af484d3743489f94de599b14d41eac656bae9a08a3a64c129104385717c861c53ce664c05038efd6", 16));
        Address from = Address.fromBase58(params, "14cdEY69ZCVCTtgebDPyqTWkAoS7SUm8fVdboh");
        Address to   = Address.fromBase58(params, "1iRbvVenrmw1SjHPTL1AviuRTJUac9iwi4HEv");

        System.out.println(Utils.HEX.encode(from.getHash160()));
        System.out.println(Utils.HEX.encode(to.getHash160()));

        ArrayList<ECKey> passportKeys = new ArrayList<ECKey>();
        passportKeys.add(passportKey);

        Wallet wallet = new Wallet(params);
        wallet.importKeys(passportKeys);

        ArrayList<Asset> assets = kit.wallet().getAvailableAssets();

        for (Asset asset : assets) {
            System.out.println(asset);
        }

        AssetBalance balance = kit.wallet().getAssetBalance(assets.get(1), from);


        Transaction transaction = new Transaction(params);


        TransactionOutput original = balance.get(0);


        System.out.println(original);
        System.out.println(Utils.HEX.encode(original.getAddressFromP2PKHScript(params).getHash160()));
        System.out.println(Utils.HEX.encode(to.getHash160()));


        System.out.println(Utils.HEX.encode(original.getScriptBytes()));

        byte[] bytes = new BigInteger("76a9140548cd8618dafdd8a8af423c4fbcb092f831d94988ac1c73706b712f497c9de31954c7651bbbb78ce13545640000000000000075", 16).toByteArray();

        TransactionOutput output = new TransactionOutput(params, transaction, Coin.ZERO, bytes);

        System.out.println(output);


        transaction.addOutput(output);
        transaction.addSignedInput(original, passportKey);

        TransactionSigner.ProposedTransaction proposal = new TransactionSigner.ProposedTransaction(transaction);
        TransactionSigner signer = new LocalTransactionSigner();

        Wallet.SendResult result = new Wallet.SendResult();
        result.tx = transaction;
        result.broadcast = kit.peerGroup().broadcastTransaction(transaction);
        result.broadcastComplete = result.broadcast.future();


        TimeUnit.SECONDS.sleep(60);
    }
}
