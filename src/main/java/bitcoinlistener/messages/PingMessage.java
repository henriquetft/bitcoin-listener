 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;

public class PingMessage implements ProtocolMessage {

	private long nonce;
	private boolean hasNonce;
	
	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public PingMessage() {
		
	}
	
	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================
	
	@Override
	public String getCommand() {
		return "ping";
	}

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.hasNonce = (buf.remaining() > 0);
			if (this.hasNonce) {
				nonce = buf.getUint64().longValue();
			}
		} finally {
			buf.setEndianness(o);
		}
	}

	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	
	public long getNonce() {
		return this.nonce;
	}
	
	public boolean hasNonce() {
		return this.hasNonce;
	}
	
	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [nonce=" + nonce + "]";
	}

}
