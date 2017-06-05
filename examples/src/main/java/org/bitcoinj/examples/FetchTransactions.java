/*
 * Copyright 2012 Google Inc.
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
import org.bitcoinj.params.MultiChainParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.utils.BriefLogFormatter;
import com.google.common.util.concurrent.ListenableFuture;

import java.net.InetAddress;
import java.util.List;

/**
 * Downloads the given transaction and its dependencies from a peers memory pool then prints them out.
 */
public class FetchTransactions {
    public static void main(String[] args) throws Exception {
        System.out.println("Connecting to node");

        final NetworkParameters params = MultiChainParams.get(
                "00b3d2e4d5269219fcc0c14912ab57ced243e4ec803fef20c09c9a5155bd15d9",
                "0100000000000000000000000000000000000000000000000000000000000000000000002cdaf384dbd47c7c28b68e77f660f5ae1a05df537c8d80909a8cf8ffd2d5b1b082573559ffff0020f30000000101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff1804ffff00200104104d756c7469436861696e20766f746534ffffffff0200000000000000002f76a9149a9023bec6d55116787ad9400e59519d01d5a70188ac1473706b703731000000000000ffffffff82573559750000000000000000131073706b6e0200040101000104726f6f74756a00000000",
                7345,
                Integer.parseInt("00ae66be", 16),
                Integer.parseInt("d90bf2fc", 16),
                0xf9dfffe4L
        );

        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);
        PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.start();
        peerGroup.addAddress(new PeerAddress(params, InetAddress.getByName("188.226.149.56")));
        peerGroup.waitForPeers(1).get();
        Peer peer = peerGroup.getConnectedPeers().get(0);

        Sha256Hash txHash = Sha256Hash.wrap("");
        ListenableFuture<Transaction> future = peer.getPeerMempoolTransaction(txHash);
        System.out.println("Waiting for node to send us the requested transaction: " + txHash);
        Transaction tx = future.get();
        System.out.println(tx);

        System.out.println("Waiting for node to send us the dependencies ...");
        List<Transaction> deps = peer.downloadDependencies(tx).get();
        for (Transaction dep : deps) {
            System.out.println("Got dependency " + dep.getHashAsString());
        }

        System.out.println("Done.");
        peerGroup.stop();
    }
}
