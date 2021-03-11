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

public class VarString implements ProtocolData {

	private String string;

	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public VarString() {
	}
	
	public VarString(String s) {
		this.string = s;
	}

	
	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			
			int length = buf.getVarIntAsInt();
			byte[] bStr = buf.getBytes(length);
			this.string = new String(bStr);

		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putVarInt(string.length());
			buf.putBytes(string.getBytes());
		} finally {
			buf.setEndianness(o);
		}
	}
	
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [string=" + string + "]";
	}
}
