package org.bitcoinj.crypto;

import org.bitcoinj.core.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
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

        byte[] part0 = this.get(0).encodeToBitcoin();
        byte[] part1 = this.get(1).encodeToBitcoin();
        byte[] part2 = this.get(2).encodeToBitcoin();
        byte[] part3 = this.get(3).encodeToBitcoin();

        System.out.println(Utils.HEX.encode(part0));
        System.out.println(Utils.HEX.encode(part1));
        System.out.println(Utils.HEX.encode(part2));
        System.out.println(Utils.HEX.encode(part3));

        byte[] result = new byte[part0.length + part1.length + part2.length + part3.length];
        System.arraycopy(part0, 0, result, 0, part0.length);
        System.arraycopy(part1, 0, result, part0.length, part1.length);
        System.arraycopy(part2, 0, result, part0.length + part1.length, part2.length);
        System.arraycopy(part3, 0, result, part0.length + part1.length + part2.length, part3.length);

        return result;
    }
}
