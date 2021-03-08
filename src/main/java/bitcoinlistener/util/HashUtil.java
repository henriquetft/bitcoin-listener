package bitcoinlistener.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
