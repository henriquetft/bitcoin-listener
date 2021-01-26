package bitcoinlistener.datatypes;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;

public class VarString implements ProtocolData {

	private String string;

	public VarString() {
	}
	
	public VarString(String s) {
		this.string = s;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			
			int length = buf.getVarIntAsInt();
			byte[] bStr = buf.getBytes(length);
			this.string = new String(bStr);

		} finally {
			buf.setEndianness(o);
		}
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putVarInt(string.length());
			buf.putBytes(string.getBytes());
		} finally {
			buf.setEndianness(o);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [string=" + string + "]";
	}
}
