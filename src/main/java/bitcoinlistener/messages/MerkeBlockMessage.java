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
 * This object describes the merkleblock message.
 * <p>
 * Provides a block header and partial merkle proof tree to show that the selected transaction
 * hashes exist in the block.
 */
public class MerkeBlockMessage implements ProtocolMessage {

	// tx: 6b3d062ee33dcb0f056bbb1b27878434546c5a1e6503ab74a885c22d1a100b98
	// [2021-07-11 19:55:07] [FINE   ] Command received: merkleblock
	// [2021-07-11 19:55:07] [FINE   ] Payload size: 119
	// [2021-07-11 19:55:07] [FINE   ] Message 'merkleblock' received (119): 0x0 0x0 0xa0 0x20 0x73 0xf4 0xf1 0x7f 0x87 0x80 0x8d 0x78 0xf9 0x3c 0x8f 0x3f 0xa9 0xb7 0xb0 0x31 0x98 0xfe 0xe1 0xa1 0xa7 0x5d 0x76 0x73 0x3f 0x0 0x0 0x0 0x0 0x0 0x0 0x0 0xf0 0x81 0x6a 0xe3 0xa9 0x63 0x69 0x92 0xaf 0x93 0x6e 0xf3 0xcc 0x3a 0xf1 0x8d 0xd2 0x3e 0x47 0x9e 0xc9 0x68 0x44 0x9e 0x66 0xfa 0xe5 0x63 0xb3 0x6e 0x39 0x4a 0xbc 0x76 0xeb 0x60 0xff 0xff 0x0 0x1a 0x90 0x2f 0x74 0x55 0x4 0x0 0x0 0x0 0x1 0xf0 0x81 0x6a 0xe3 0xa9 0x63 0x69 0x92 0xaf 0x93 0x6e 0xf3 0xcc 0x3a 0xf1 0x8d 0xd2 0x3e 0x47 0x9e 0xc9 0x68 0x44 0x9e 0x66 0xfa 0xe5 0x63 0xb3 0x6e 0x39 0x4a 0x1 0x0

	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public MerkeBlockMessage() {

	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {

	}

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String getCommand() {
		return "merkleblock";
	}

	// =============================================================================================
	/// ACCESSORS (GETTERS AND SETTERS)
	// =============================================================================================



	// =============================================================================================
	// OBJECT OPERATIONS
	// =============================================================================================

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
