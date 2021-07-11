/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;

/**
 * The verack message is sent in reply to version. This message consists of only
 * a message header with the command string "verack".
 */
public class Verack implements ProtocolMessage {

	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	public Verack() {
		
	}
	
	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================
	
	public byte[] getBytes() {
		return new byte[0];
	}

	@Override
	public String getCommand() {
		return "verack";
	}

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		
	}
}
