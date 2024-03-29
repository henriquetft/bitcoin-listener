/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import bitcoinlistener.BitcoinBuffer;

import java.nio.ByteOrder;
import java.util.List;

/**
 * This object describes a bitcoin block.
 * <p>
 * The block message is sent in response to a getdata message which requests transaction information
 * from a block hash.
 */
public class BlockMessage extends AbstractBlockMessage {

	/**
	 * Block transactions
	 */
	private List<TxMessage> txList; // tx[]

	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public BlockMessage() {

	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		super.loadFromBuffer(buf);
		ByteOrder o = buf.getEndianness();
		try {
			txList = buf.getVector(TxMessage.class);
		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String getCommand() {
		return "block";
	}

	// =============================================================================================
	/// ACCESSORS (GETTERS AND SETTERS)
	// =============================================================================================


	public List<TxMessage> getTxList() {
		return txList;
	}

	public void setTxList(List<TxMessage> txList) {
		this.txList = txList;
	}

	// =============================================================================================
	// OBJECT OPERATIONS
	// =============================================================================================

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hash=" + getHashAsStr() + ", version=" + getVersion() +
			   ", prevBlock=" + getPrevBlock() + ", merkleRoot=" + getMerkleRoot() +
			   ", timestamp=" + getTimestamp() + ", bits=" + getBits() + ", nonce=" + getNonce() + "]";
	}
}
