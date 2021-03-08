package bitcoinlistener.messages;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;

public class FilterLoadMessage implements ProtocolMessage {


//	 ? 	filter 	uint8_t[] 	The filter itself is simply a bit field of arbitrary byte-aligned size. The maximum size is 36,000 bytes.
//			 4 	nHashFuncs 	uint32_t 	The number of hash functions to use in this filter. The maximum value allowed in this field is 50.
//			 4 	nTweak 	uint32_t 	A random value to add to the seed value in the hash function used by the bloom filter.
//			 1 	nFlags 	uint8_t 	A set of flags that control how matched items are added to the filter. 
	
	private byte[] filter;   // uint8_t[]
	private long numHashFuncs; // uint32_t (4)
	private long tweak;     // uint32_t (4)
	private byte flags;     // uint8_t (1)

	public FilterLoadMessage(byte[] filter, long numHashFuncs, long tweak, byte flags) {
		super();
		this.filter = filter;
		this.numHashFuncs = numHashFuncs;
		this.tweak = tweak;
		this.flags = flags;
	}
	
	public FilterLoadMessage() {
		
	}
	
	@Override
	public String getCommand() {
		return "filterload";
	}
	
	@Override
	public byte[] getBytes() {
		BitcoinBuffer buf = new BitcoinBuffer(4 + 4 + 1 + filter.length + 9);
		buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
		buf.putVarInt(this.filter.length);
		buf.putBytes(this.filter);
		buf.putUint32(this.numHashFuncs);
		buf.putUint32(this.tweak);
		buf.putByte(this.flags);
		byte[] arr = buf.toArrayExactSize();
		return arr;
	}
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			int size = buf.getVarIntAsInt();
			this.filter = buf.getBytes(size);

			this.numHashFuncs = buf.getUint32();
			this.tweak = buf.getUint32();
			this.flags = buf.getByte();
		} finally {
			buf.setEndianness(old);
		}
	}

	public byte[] getFilter() {
		return filter;
	}

	public void setFilter(byte[] filter) {
		this.filter = filter;
	}

	public long getNumHashFuncs() {
		return numHashFuncs;
	}

	public void setNumHashFuncs(long nHashFuncs) {
		this.numHashFuncs = nHashFuncs;
	}

	public long getTweak() {
		return tweak;
	}

	public void setTweak(long nTweak) {
		this.tweak = nTweak;
	}

	public byte getFlags() {
		return flags;
	}

	public void setFlags(byte nFlags) {
		this.flags = nFlags;
	}
}
