package bitcoinlistener.util;

import java.util.BitSet;

/**
 * A bloom filter using java.util.BitSet and MurmurHash3
 */
public class BloomFilter {

	private static final long MAX_FILTER_SIZE = 36000;
	private static final int MAX_NUM_HASH_FUNCS = 50;

	private BitSet bitset;
	private int numberOfHashFuncs;
	private long nonce;
	private int nbits;
	private int sizeInBytes;

	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public BloomFilter(int numElements, double falsePositiveRate, long nonce) {
		sizeInBytes = (int) (-1 / (Math.pow(Math.log(2), 2)) * numElements * 
				Math.log(falsePositiveRate));
		sizeInBytes = Math.max(1, Math.min(sizeInBytes, (int) MAX_FILTER_SIZE * 8) / 8);
		this.nbits = sizeInBytes * 8;
		this.bitset = new BitSet();

		numberOfHashFuncs = (int) (nbits / (double) numElements * Math.log(2));
		numberOfHashFuncs = Math.max(1, Math.min(numberOfHashFuncs, MAX_NUM_HASH_FUNCS));
		this.nonce = nonce;
	}

	// =============================================================================================
	// OPERATIONS                                                                                   
	// =============================================================================================
	
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
	
	public byte[] getAsArray() {
		byte[] result = new byte[sizeInBytes];
		// the returned byte array can be smaller than sizeInBytes
		byte[] bitsetArr = bitset.toByteArray();
		System.arraycopy(bitsetArr, 0, result, 0, bitsetArr.length);
		return result;
	}

	// =============================================================================================
	/// ACCESSORS (GETTERS AND SETTERS)                                                             
	// =============================================================================================
	
	public int getNumberOfHashFuncs() {
		return numberOfHashFuncs;
	}

	public long getNonce() {
		return nonce;
	}

	public int getNbits() {
		return nbits;
	}
}
