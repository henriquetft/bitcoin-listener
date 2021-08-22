/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.datatypes;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;
import bitcoinlistener.util.ByteUtil;

import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Wrapper for array of bytes containing a sha-256 hash.
 */
public class SHA256Hash implements ProtocolData {

	private static int LENGTH = 32;

	private byte[] hash;

	public SHA256Hash() {

	}
	public SHA256Hash(byte[] hash) {
		this.hash = hash;
	}


	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)
	// =============================================================================================

	public String getHashAsStr() {
		return ByteUtil.byteArrayToStr(hash);
	}

	public byte[] getHash() {
		return hash;
	}

	public SHA256Hash getInverted() {
		return new SHA256Hash(ByteUtil.getInvertedArray(this.hash));
	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
		try {
			this.hash = buf.getBytes(LENGTH);
			ByteUtil.invertArray(this.hash);
		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		throw new RuntimeException("not implemented");
	}

	// =============================================================================================
	// OBJECT OPERATIONS
	// =============================================================================================


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SHA256Hash that = (SHA256Hash) o;
		return Arrays.equals(hash, that.hash);
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hashAsStr=" + getHashAsStr() + "]";
	}
}
