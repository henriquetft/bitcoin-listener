 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import java.util.Arrays;

public class AddressUtil {
	
	/**
	 * Returns hash160-20-byte for a base58-encoded bitcoin addresses
	 * 
	 * @param addrBase58 Address encoded as base58
	 * @return hash160(pubkey) for P2PKH or hash160(script) for P2SH
	 */
	public static byte[] getAddrHash(String addrBase58) {
		byte[] l = Base58.decodeChecked(addrBase58);
		byte[] bytes = Arrays.copyOfRange(l, 1, l.length);
		return bytes;
	}
}
