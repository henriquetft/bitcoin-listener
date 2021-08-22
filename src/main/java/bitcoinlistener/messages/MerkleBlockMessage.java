/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.datatypes.SHA256Hash;
import bitcoinlistener.util.PartialMerkleTree;

import java.nio.ByteOrder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This object describes the merkleblock message.
 * <p>
 * Provides a block header and partial merkle proof tree to show that the selected transaction
 * hashes exist in the block.
 */
public class MerkleBlockMessage extends AbstractBlockMessage {

	/**
	 * Number of transactions in the block (including unmatched ones)
	 */
	private long totalTransactions;    // uint32_t

	/**
	 * Hashes in depth-first order
	 */
	private List<SHA256Hash> hashes;   // char[32]

	/**
	 * Flag bits, packed per 8 in a byte, least significant bit first. Extra 0 bits are padded
	 * on to reach full byte size.
	 */
	private byte[] flags;

	private PartialMerkleTree partialMerkleTree;

	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public MerkleBlockMessage() {

	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		super.loadFromBuffer(buf);
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.totalTransactions = buf.getUint32();
			// invert hashes bytes
			this.hashes = buf.getVector(SHA256Hash.class).stream().map(x -> x.getInverted()).collect(
					Collectors.toList());
			this.flags = buf.getBytes(buf.getVarInt().intValue());
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
		return "merkleblock";
	}

	// =============================================================================================
	/// ACCESSORS (GETTERS AND SETTERS)
	// =============================================================================================


	public long getTotalTransactions() {
		return totalTransactions;
	}

	public void setTotalTransactions(long totalTransactions) {
		this.totalTransactions = totalTransactions;
	}

	public List<SHA256Hash> getHashes() {
		return hashes;
	}

	public void setHashes(List<SHA256Hash> hashes) {
		this.hashes = hashes;
	}

	public byte[] getFlags() {
		return flags;
	}

	public void setFlags(byte[] flags) {
		this.flags = flags;
	}

	public PartialMerkleTree getPartialMerkleTree() {
		return partialMerkleTree;
	}

	public void setPartialMerkleTree(PartialMerkleTree partialMerkleTree) {
		this.partialMerkleTree = partialMerkleTree;
	}

	// =============================================================================================
	// OBJECT OPERATIONS
	// =============================================================================================

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hash=" + getHashAsStr() + ", version=" + getVersion() +
			   ", totalTransactions=" + totalTransactions +
			   ", prevBlock=" + getPrevBlock() + ", merkleRoot=" + getMerkleRoot() +
			   ", timestamp=" + getTimestamp() + ", bits=" + getBits() + ", nonce=" + getNonce() + "]";
	}
}
