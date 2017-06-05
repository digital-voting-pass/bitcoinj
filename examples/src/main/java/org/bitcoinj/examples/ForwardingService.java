/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
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

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.MultiChainParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.File;
import java.net.InetAddress;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ForwardingService demonstrates basic usage of the library. It sits on the network and when it receives coins, simply
 * sends them onwards to an address given on the command line.
 */
public class ForwardingService {
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
                "00b3d2e4d5269219fcc0c14912ab57ced243e4ec803fef20c09c9a5155bd15d9",
                "0100000000000000000000000000000000000000000000000000000000000000000000002cdaf384dbd47c7c28b68e77f660f5ae1a05df537c8d80909a8cf8ffd2d5b1b082573559ffff0020f30000000101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff1804ffff00200104104d756c7469436861696e20766f746534ffffffff0200000000000000002f76a9149a9023bec6d55116787ad9400e59519d01d5a70188ac1473706b703731000000000000ffffffff82573559750000000000000000131073706b6e0200040101000104726f6f74756a00000000",
                7345,
                Integer.parseInt("00ae66be", 16),
                Integer.parseInt("d90bf2fc", 16),
                0xf9dfffe4L
        );

        String filePrefix = "forwarding-service21" + Math.round(Math.random() * 100);

        // Parse the address given as the first parameter.
        forwardingAddress = Address.fromBase58(params, args[0]);

        // Start up a basic app using a class that automates some boilerplate.
        kit = new WalletAppKit(params, new File("."), filePrefix);

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        kit.setPeerNodes(new PeerAddress(params, InetAddress.getByName("188.226.149.56")));

        // Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();

        Wallet wallet = kit.wallet();
        Address sendToAddress = wallet.currentReceiveKey().toAddress(params);

        System.out.println("Address: " + sendToAddress + ", balance: " + wallet.getBalance());

    }


}
