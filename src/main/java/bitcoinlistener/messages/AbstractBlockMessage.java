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
import bitcoinlistener.datatypes.SHA256Hash;
import bitcoinlistener.util.ByteUtil;
import bitcoinlistener.util.HashUtil;

import java.nio.ByteOrder;

/**
 * Base class for {@link BlockMessage} and @{link {@link MerkleBlockMessage}
 */
public abstract class AbstractBlockMessage implements ProtocolMessage {

	/**
	 * Block version information (note, this is signed)
	 */
	private int version;                // int32_t

	/**
	 * The hash value of the previous block this particular block references
	 */
	private SHA256Hash prevBlock;       // char[32]

	/**
	 * The reference to a Merkle tree collection which is a hash of all transactions related to this
	 * block
	 */
	private SHA256Hash merkleRoot;      // char[32]

	/**
	 * A Unix timestamp recording when this block was created (Currently limited to dates before the
	 * year 2106!)
	 */
	private long timestamp;              // uint32_t

	/**
	 * The calculated difficulty target being used for this block
	 */
	private long bits;                    // uint32_t

	/**
	 * The nonce used to generate this block… to allow variations of the header and compute
	 * different hashes
	 */
	private long nonce;                    // uint32_t


	/**
	 * Raw data of block header
	 */
	private byte[] headerBlockData;

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			int startPosHeader = buf.getPosition();
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.version = buf.getInt32();
			this.prevBlock = buf.getData(SHA256Hash.class);
			this.merkleRoot = buf.getData(SHA256Hash.class);
			this.timestamp = buf.getUint32();
			this.bits = buf.getUint32();
			this.nonce = buf.getUint32();
			int endPosHeader = buf.getPosition();
			int endPos = buf.getPosition();

			buf.setPosition(startPosHeader);
			headerBlockData = buf.getBytes(endPosHeader - startPosHeader);

			buf.setPosition(endPos);

		} finally {
			buf.setEndianness(o);
		}
	}

	public String getHashAsStr() {
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

	public SHA256Hash getPrevBlock() {
		return prevBlock;
	}


	public void setPrevBlock(SHA256Hash prevBlock) {
		this.prevBlock = prevBlock;
	}

	public SHA256Hash getMerkleRoot() {
		return merkleRoot;
	}

	public void setMerkleRoot(SHA256Hash merkleRoot) {
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
}
