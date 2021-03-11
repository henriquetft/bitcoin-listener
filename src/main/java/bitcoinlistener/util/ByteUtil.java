 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class ByteUtil {

	public static boolean compareArray(byte[] big, int posBig, byte[] small, int number) {
		for (int x = 0; x < number; x++) {
			if (big[x + posBig] != small[x]) {
				return false;
			}
		}
		return true;
	}

	public static int intFromBytes(byte[] bytes) {
		return ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8)
				| ((bytes[0] & 0xFF) << 0);
	}

	public static byte[] slice(byte[] arr, int pos, int size) {
		byte[] ret = new byte[size];
		System.arraycopy(arr, pos, ret, 0, size);
		return ret;
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static long getRandomNonce() {
		byte[] nonce = new byte[8];
		new SecureRandom().nextBytes(nonce);
		return Math.abs(bytesToLong(nonce));
	}

	public static String toHexString(byte[] arr) {
		StringBuilder hexString = new StringBuilder(2 * arr.length);
		for (int i = 0; i < arr.length; i++) {
			String hex = Integer.toHexString(0xff & arr[i]);
			hexString.append("0x");
			hexString.append(hex);
			hexString.append(" ");
		}
		return hexString.toString();
	}

	public static int findByte(byte[] command, byte b) {
		for (int i = 0; i < command.length; i++) {
			if (command[i] == b) {
				return i;
			}
		}
		return -1;
	}

	public static void invertArray(byte[] arr) {
		for (int x = 0; x < arr.length / 2; x++) {
			byte aux = arr[x];
			arr[x] = arr[arr.length - 1 - x];
			arr[arr.length - 1 - x] = aux;
		}
	}

	public static byte[] getInvertedArray(byte[] arr) {
		byte[] data = new byte[arr.length];
		System.arraycopy(arr, 0, data, 0, data.length);
		invertArray(data);
		return data;
	}

	public static String byteArrayToStr(byte[] barr) {
		StringBuilder hex = new StringBuilder(barr.length * 2);
		for (byte b : barr) {
			hex.append(String.format("%02x", b));
		}
		return hex.toString();
	}

}
