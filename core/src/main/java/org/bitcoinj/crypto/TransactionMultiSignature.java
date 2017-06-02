package org.bitcoinj.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Creates a list of multiple signatures, which make it possible to sign a transaction in 4 parts
 */
public class TransactionMultiSignature extends ArrayList<TransactionSignature> {

    /**
     * What we get back from the signer are the two components of a signature, r and s. To get a flat byte stream
     * of the type used by Bitcoin we have to encode them using DER encoding, which is just a way to pack the two
     * components into a structure, and then we append a byte to the end for the sighash flags.
     */
    public byte[] encodeToBitcoin() {
        byte[] result = new byte[0];

        System.out.println(this.get(0).encodeToBitcoin().length);

        return result;

    }

}
