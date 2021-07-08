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

import java.nio.ByteOrder;

/**
 * The pong message is sent in response to a ping message.
 */
public class PongMessage implements ProtocolMessage {

	/** nonce from ping */
	private long nonce; // uint64_t


	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public PongMessage() {

	}

	public PongMessage(long nonce) {
		this.nonce = nonce;
	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	@Override
	public String getCommand() {
		return "pong";
	}

	@Override
	public byte[] getBytes() {
		BitcoinBuffer buf = new BitcoinBuffer(8);
		buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
		buf.putUint64(nonce);
		byte[] arr = buf.toArrayExactSize();
		return arr;
	}

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		throw new RuntimeException();
	}

	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)
	// =============================================================================================


	public long getNonce() {
		return this.nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}


	// =============================================================================================
	// OBJECT OPERATIONS
	// =============================================================================================

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [nonce=" + nonce + "]";
	}

}
