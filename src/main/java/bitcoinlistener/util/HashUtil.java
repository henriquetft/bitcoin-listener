 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

 public class HashUtil {
	
	public static byte[] sha256(byte[] arr) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(arr);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] sha256(byte[] first, byte[] second) {
		byte[] both = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, both, first.length, second.length);
		return sha256(both);
	}

	public static int murmurHash3(int nbits, long nTweak, int hashNum, byte[] data) {
		int h1 = MurmurHash3.murmurhash3x8632(data, 0, data.length, (int) (hashNum * 0xFBA4C795L + nTweak));
		return (int) ((h1 & 0xFFFFFFFFL) % nbits);
	}

	public static byte[] sha256(byte[] data, int offset, int length) {
		byte[] arr = new byte[length-offset];
		System.arraycopy(data, 0, arr, 0, arr.length);
		return sha256(arr);
	}
}
