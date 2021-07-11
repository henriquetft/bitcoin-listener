/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.datatypes;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;
import bitcoinlistener.util.ByteUtil;

/**
 * Inventory Object for {@link bitcoinlistener.messages.InvMessage}
 */
public class InvObject implements ProtocolData {
	
	private int type;           // uint32_t
	private byte[] hash;        // char[32] 
	private String hashAsStr;
	
	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public InvObject() {
		
	}
	
	public InvObject(int type, byte[] hashObjArr) {
		this.type = type;
		this.hash = hashObjArr;
	}


	// =============================================================================================
	// OPERATIONS                                                                                
	// =============================================================================================
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			int type = buf.getInt32();
			byte[] hashObjArr = buf.getBytes(32);

			this.type = type;
			this.hash = hashObjArr;

		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putInt32(this.type);
			buf.putBytes(hash);
		} finally {
			buf.setEndianness(o);
		}
		
	}
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}


	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public String getHashAsStr() {
		byte[] arr = new byte[hash.length];
		int i = 0;
		for (int x = hash.length-1; x >= 0; x--) {
			arr[i++] = hash[x];
		}
	
		this.hashAsStr = ByteUtil.byteArrayToStr(arr);
		return this.hashAsStr;
	}
	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [type=" + type + ", hashAsStr=" + hashAsStr + "]";
	}

}
