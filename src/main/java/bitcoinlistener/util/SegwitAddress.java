/*
 * Copyright by the original author or authors.
 *
 * Modifications Copyright 2021 Henrique Teófilo
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

/*
 * This code was borrowed from bitcoinj project
 * 
 * The original code was changed to remove some dependencies
 * in order to run outside bitcoinj.
 *  
 */

package bitcoinlistener.util;

import java.io.ByteArrayOutputStream;


public class SegwitAddress {

    /**
     * Helper for re-arranging bits into groups.
     */
    public static byte[] convertBits(final byte[] in, final int inStart, final int inLen,
                                     final int fromBits,
                                     final int toBits, final boolean pad) throws RuntimeException {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i + inStart] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new RuntimeException(
                        String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0)
                out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new RuntimeException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }

    /**
     * Returns the witness program in decoded form.
     * 
     * @return witness program
     */
    public static byte[] getWitnessProgram(byte[] bytes) {
        // skip version byte
        return convertBits(bytes, 1, bytes.length - 1, 5, 8, false);
    }
}
