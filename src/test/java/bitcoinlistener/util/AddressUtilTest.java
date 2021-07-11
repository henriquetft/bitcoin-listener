/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddressUtilTest {

	@Test
	public void testGetAddrHash() {
		String expected = "6eafa604a503a0bb445ad1f6daa80f162b5605d6";

		String addrBase58 = "1B6FkNg199ZbPJWG5zjEiDekrCc2P7MVyC";
		assertEquals(expected, ByteUtil.bytesToHex(AddressUtil.getAddrHash(addrBase58)));
		
		String addrBech32 = "bc1qd6h6vp99qwstk3z668md42q0zc44vpwkk824zh";
		assertEquals(expected, ByteUtil.bytesToHex(AddressUtil.getAddrHash(addrBech32)));
	}
}
