 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.datatypes.TxIn;
import bitcoinlistener.datatypes.TxOut;
import bitcoinlistener.util.ByteUtil;

public class TxMessageTest {
	
	@Test
	public void testar() {
		String msg = "0x1 0x0 0x0 0x0 0x1 0x7 0x8d 0x59 0x9 0x41 0xcc 0x46 0xb5 0xf7 0x89 0xf0 0xd 0x50 0x0 0x86 0xaa 0xc 0x34 0x40 0xbd 0x75 0x8 0x52 0x17 0xf4 0x9c 0x15 0x8f 0x7d 0x24 0xb5 0x95 0x3 0x0 0x0 0x0 0x6b 0x48 0x30 0x45 0x2 0x21 0x0 0x9d 0x57 0xb2 0x5d 0xf7 0x2a 0xf9 0x85 0x82 0x1c 0xff 0xc1 0x5a 0xde 0x36 0xb7 0x2b 0xad 0x38 0x55 0xcc 0x4b 0xa7 0xb3 0x4a 0x3d 0xa3 0x6d 0x48 0x64 0xdc 0x64 0x2 0x20 0x3 0x93 0xe3 0xed 0x36 0xd3 0x89 0xf2 0x4b 0xc7 0xa3 0xfb 0xe9 0x9a 0x53 0x61 0x44 0x53 0x1f 0xd5 0x64 0x91 0xec 0xf9 0x79 0xdb 0xc9 0x9a 0xcd 0xf6 0x25 0x4f 0x1 0x21 0x2 0xf3 0x9a 0x63 0x6b 0x5d 0xb4 0x27 0xe5 0x65 0x3b 0x4b 0xd1 0x21 0xbe 0x31 0x1a 0x20 0x1d 0xb8 0xb8 0x11 0xd2 0x7e 0x4f 0x68 0x82 0x47 0x38 0xd4 0x95 0x12 0x55 0xfd 0xff 0xff 0xff 0x4 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x53 0x6a 0x4c 0x50 0x58 0x33 0x5b 0xc7 0x8d 0xb6 0xa8 0xad 0x86 0x55 0x10 0x54 0xf5 0x67 0xf 0x59 0x46 0x9a 0xf 0x29 0xdb 0xc4 0x1e 0x84 0xdf 0x4a 0x83 0xfa 0x4d 0xca 0xb5 0x14 0x58 0xb2 0x12 0x73 0x2c 0xd6 0x86 0xf3 0x67 0xa2 0xbc 0x70 0x12 0xc4 0xb3 0x9d 0x1c 0xca 0x6b 0x2c 0x94 0x44 0xa6 0x88 0x67 0x62 0x8 0x6e 0x14 0xb2 0xb7 0x4 0x4e 0x43 0x2f 0x0 0x1d 0x14 0xd8 0x0 0x13 0x0 0x1d 0x8 0xbc 0x0 0x65 0x1 0x10 0x27 0x0 0x0 0x0 0x0 0x0 0x0 0x19 0x76 0xa9 0x14 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x88 0xac 0x10 0x27 0x0 0x0 0x0 0x0 0x0 0x0 0x19 0x76 0xa9 0x14 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0x88 0xac 0xd1 0x79 0x60 0x1 0x0 0x0 0x0 0x0 0x19 0x76 0xa9 0x14 0x1 0xb7 0x92 0x58 0x5c 0x31 0xf 0x4 0x30 0x1c 0x73 0x3a 0x97 0x51 0x55 0x5e 0x1f 0x26 0xb4 0x1d 0x88 0xac 0x0 0x0 0x0 0x0";
		msg = msg.replaceAll("0x", "").trim();
		String[] arr = msg.split(" ");
		byte[] aaa = new byte[arr.length];
		System.out.println("SIZE TX: " + aaa.length);
		
		
		int x = 0;
		for (String string : arr) {
			int decimal = Integer.parseInt(string,16);
			aaa[x++] = (byte) decimal;
		}
		System.out.println(Arrays.toString(aaa));
		System.out.println("TRANSACTION: " + ByteUtil.byteArrayToStr(aaa));
		
		BitcoinBuffer buffer = new BitcoinBuffer(aaa);
		TxMessage tx = new TxMessage();
		tx.loadFromBuffer(buffer);
		System.out.println(tx);
		List<TxIn> list = tx.getTxInList();
		for (TxIn txIn : list) {
			System.out.println(txIn);
		}
		for (TxOut txOut : tx.getTxOutList()) {
			System.out.println(txOut);
		}
		
		
		byte[] rr = tx.getBytes();
		System.out.println("SERIALIZED: " + ByteUtil.bytesToHex(rr));

	}
}
