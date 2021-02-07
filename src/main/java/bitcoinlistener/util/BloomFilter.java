package bitcoinlistener.util;

import java.util.BitSet;

/**
 * A bloom filter using BitSet and MurmurHash3
 * 
 */
public class BloomFilter {

	private static final long MAX_FILTER_SIZE = 36000;
	private static final int MAX_NUM_HASH_FUNCS = 50;

	private BitSet bitset;
	private int numberOfHashFuncs;
	private long nonce;
	private int nbits;

	public BloomFilter(int numElements, double falsePositiveRate, long nonce) {
		int size = (int) (-1 / (Math.pow(Math.log(2), 2)) * numElements * Math.log(falsePositiveRate));
		size = Math.max(1, Math.min(size, (int) MAX_FILTER_SIZE * 8) / 8);
		this.nbits = size * 8;
		this.bitset = new BitSet(nbits);
		numberOfHashFuncs = (int) (nbits / (double) numElements * Math.log(2));
		numberOfHashFuncs = Math.max(1, Math.min(numberOfHashFuncs, MAX_NUM_HASH_FUNCS));
		this.nonce = nonce;
	}

	public void insert(byte[] data) {
		for (int x = 0; x < numberOfHashFuncs; x++) {
			int bitIndex = HashUtil.murmurHash3(nbits, nonce, x, data);
			bitset.set(bitIndex);
		}
	}

	public boolean contains(byte[] data) {
		for (int i = 0; i < numberOfHashFuncs; i++) {
			int bitIndex = HashUtil.murmurHash3(nbits, nonce, i, data);
			if (!bitset.get(bitIndex)) {
				return false;
			}
		}
		return true;
	}

	public int getNumberOfHashFuncs() {
		return numberOfHashFuncs;
	}

	public void setNumberOfHashFuncs(int nHashFuncs) {
		this.numberOfHashFuncs = nHashFuncs;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public int getNbits() {
		return nbits;
	}

	public void setNbits(int nbits) {
		this.nbits = nbits;
	}
	
	public byte[] getArray() {
		return bitset.toByteArray();
	}
}
