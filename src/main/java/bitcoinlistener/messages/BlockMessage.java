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
import bitcoinlistener.util.ByteUtil;
import bitcoinlistener.util.HashUtil;

import java.nio.ByteOrder;
import java.util.List;

/**
 * This object describes a bitcoin block.
 * <p>
 * The block message is sent in response to a getdata message which requests transaction information
 * from a block hash.
 */
public class BlockMessage implements ProtocolMessage {

	/**
	 * Block version information (note, this is signed)
	 */
	private int version;            // int32_t

	/**
	 * The hash value of the previous block this particular block references
	 */
	private byte[] prevBlock;       // char[32]

	/**
	 * The reference to a Merkle tree collection which is a hash of all transactions related to this
	 * block
	 */
	private byte[] merkleRoot;      // char[32]

	/**
	 * A Unix timestamp recording when this block was created (Currently limited to dates before the
	 * year 2106!)
	 */
	private long timestamp;         // uint32_t

	/**
	 * The calculated difficulty target being used for this block
	 */
	private long bits;              // uint32_t

	/**
	 * The nonce used to generate this block… to allow variations of the header and compute
	 * different hashes
	 */
	private long nonce;             // uint32_t

	/**
	 * Block transactions
	 */
	private List<TxMessage> txList; // tx[]

	/**
	 * Raw data of block header
	 */
	private byte[] headerBlockData;

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
		ByteOrder o = buf.getEndianness();
		try {
			int startPosHeader = buf.getPosition();
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.version = buf.getInt32();
			this.prevBlock = buf.getBytes(32);
			this.merkleRoot = buf.getBytes(32);
			this.timestamp = buf.getUint32();
			this.bits = buf.getUint32();
			this.nonce = buf.getUint32();
			int endPosHeader = buf.getPosition();
			txList = buf.getVector(TxMessage.class);

			ByteUtil.invertArray(this.prevBlock);
			ByteUtil.invertArray(this.merkleRoot);

			buf.setPosition(startPosHeader);
			headerBlockData = buf.getBytes(endPosHeader - startPosHeader);

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

	public String getHash() {
		byte[] data = new byte[headerBlockData.length];
		System.arraycopy(headerBlockData, 0, data, 0, data.length);

		data = HashUtil.sha256(HashUtil.sha256(data));
		ByteUtil.invertArray(data);
		return ByteUtil.bytesToHex(data);
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public byte[] getPrevBlock() {
		return prevBlock;
	}

	public String getPrevBlockHash() {
		return ByteUtil.byteArrayToStr(prevBlock);
	}

	public void setPrevBlock(byte[] prevBlock) {
		this.prevBlock = prevBlock;
	}

	public byte[] getMerkleRoot() {
		return merkleRoot;
	}

	public String getMerkleRootHash() {
		return ByteUtil.byteArrayToStr(merkleRoot);
	}

	public void setMerkleRoot(byte[] merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getBits() {
		return bits;
	}

	public void setBits(long bits) {
		this.bits = bits;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

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
		return getClass().getSimpleName() + " [hash=" + getHash() + ", version=" + version +
			   ", prevBlock=" + getPrevBlockHash() + ", merkleRoot=" + getMerkleRootHash() +
			   ", timestamp=" + timestamp + ", bits=" + bits + ", nonce=" + nonce + "]";
	}
}
