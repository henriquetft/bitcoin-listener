package bitcoinlistener.datatypes;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;
import bitcoinlistener.util.ByteUtil;

public class OutPoint implements ProtocolData {

	private byte[] hash; // char[32]
	private long index; // UINT32 (4)

	public byte[] getHash() {
		return hash;
	}


	public void setHash(byte[] hash) {
		this.hash = hash;
	}


	public long getIndex() {
		return index;
	}


	public void setIndex(long index) {
		this.index = index;
	}


	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.hash = buf.getBytes(32);
			ByteUtil.invertArray(this.hash);
			this.index = buf.getUint32();
		} finally {
			buf.setEndianness(o);
		}
	}
	

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putBytes(ByteUtil.getInvertedArray(this.hash));
			buf.putUint32(this.index);
		} finally {
			buf.setEndianness(old);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hash=" + ByteUtil.byteArrayToStr(hash) + ", index=" + index + "]";
	}
}
