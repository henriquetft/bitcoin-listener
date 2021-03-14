 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import java.util.Arrays;

import bitcoinlistener.BitcoinListenerException;
import bitcoinlistener.util.Bech32.Bech32Data;

public class AddressUtil {
	

	public static byte[] getAddrHash(String address) {
		byte[] bytes = null;
		try {
			bytes = getHashFromBase58Address(address);
		} catch (Exception e) {
			try {
				bytes = getHashFromBech32Address(address);
			} catch (Exception e1) {
				throw new BitcoinListenerException("Unrecognized address format");
			}
		}
		return bytes;
	}
	
	/**
	 * Returns hash160-20-byte for a base58-encoded bitcoin addresses
	 * 
	 * @param addrBase58 Address encoded as base58
	 * @return hash160(pubkey) for P2PKH or hash160(script) for P2SH
	 */
	private static byte[] getHashFromBase58Address(String addrBase58) {
		byte[] l = Base58.decodeChecked(addrBase58);
		byte[] bytes = Arrays.copyOfRange(l, 1, l.length);
		return bytes;
	}
	
	/**
	 * Returns hash160-20-byte for a bech32-encoded bitcoin addresses
	 * 
	 * @param bech32Address Address encoded as bech32
	 * @return hthe witness program
	 */
	private static byte[] getHashFromBech32Address(String bech32Address) {
		Bech32Data b = Bech32.decode(bech32Address);
		byte[] arr = SegwitAddress.getWitnessProgram(b.data);
		return arr;
	}

}
