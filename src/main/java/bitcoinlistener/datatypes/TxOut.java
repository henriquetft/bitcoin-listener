 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.datatypes;

import java.math.BigInteger;
import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;
import bitcoinlistener.util.ByteUtil;

public class TxOut implements ProtocolData {

	private long value;      // int64_t
	private byte[] pkScript; // var_int + uchar[]

	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================

	public TxOut() {
		
	}

	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.value = buf.getInt64();
			BigInteger pkScriptLength = buf.getVarInt();
			this.pkScript = buf.getBytes(pkScriptLength.intValue());
			;

		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putInt64(this.value);
			buf.putVarInt(pkScript.length);
			buf.putBytes(pkScript);
		} finally {
			buf.setEndianness(old);
		}
	}
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	public byte[] getPkScript() {
		return pkScript;
	}

	public void setPkScript(byte[] pkScript) {
		this.pkScript = pkScript;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}


	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [value=" + value + ", pkScript=" +
				ByteUtil.byteArrayToStr(pkScript) + "]";
	}
}
