/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

/**
 * The ProtocolMessage interface is the root interface of all Bitcoin protocol messages.
 */
public interface ProtocolMessage extends ProtocolData {

	/**
	 * Identifies the message type
	 */
	String getCommand();

	/**
	 * Converts this message object to bytes to be sent over wire (serialize)
	 */
	byte[] getBytes();

	/**
	 * Converts bytes to this message object (deserialize)
	 *
	 * @param buf the buffer to read bytes from
	 */
	void loadFromBuffer(BitcoinBuffer buf);

	/**
	 * Serialize.
	 *
	 * @param buf the buffer to write to
	 */
	default void writeToBuffer(BitcoinBuffer buf) {
		buf.putBytes(getBytes());
	}
}
