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
 * It points to a specific output in a transaction.
 */
public class OutPoint implements ProtocolData {

	/** The hash of the referenced transaction. */
	private byte[] hash; // char[32]
	
	/** The index of the specific output in the transaction. The first output is 0, etc. */
	private long index;  // UINT32 (4)

	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================

	public OutPoint() {
		
	}

	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.hash = buf.getBytes(32);
			ByteUtil.invertArray(this.hash);
			this.index = buf.getUint32();
		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putBytes(ByteUtil.getInvertedArray(this.hash));
			buf.putUint32(this.index);
		} finally {
			buf.setEndianness(old);
		}
	}

	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}
	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hash=" + ByteUtil.byteArrayToStr(hash) +
				", index=" + index + "]";
	}
}
