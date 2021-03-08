package bitcoinlistener.util;

import java.util.Arrays;

public class AddressUtil {
	public static byte[] getAddrHash(String address) {
		byte[] l = Base58.decodeChecked(address);
		byte[] bytes = Arrays.copyOfRange(l, 1, l.length);
		return bytes;
	}
}
