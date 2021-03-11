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
	public static byte[] getAddrHash(String address) {
		byte[] l = Base58.decodeChecked(address);
		byte[] bytes = Arrays.copyOfRange(l, 1, l.length);
		return bytes;
	}
}
