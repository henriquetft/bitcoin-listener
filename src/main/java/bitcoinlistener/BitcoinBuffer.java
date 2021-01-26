package bitcoinlistener;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.util.ByteUtil;
import bitcoinlistener.util.MyBuffer;

/**
 * A byte buffer that helps sending and receiving bitcoin protocol messages.
 * 
 */
public class BitcoinBuffer extends MyBuffer {

	public BitcoinBuffer(int initialCapacity) {
		super(initialCapacity);
	}
	
	public BitcoinBuffer(byte[] array) {
		super(array);
	}

	
	public void putData(ProtocolData data) {
		data.writeToBuffer(this);
	}
	
	
	public void putVarInt(BigInteger v) {
		byte[] varIntBytes = BitcoinBuffer.getVarInt(v);
		buf.put(varIntBytes);
	}
	
	public void putVarInt(int v) {
		putVarInt(BigInteger.valueOf(v));
	}
	
	public BigInteger getVarInt() {
		return readVarInt(buf);
	}
	
	public long getVarIntAsLong() {
		return getVarInt().longValue();
	}
	
	public int getVarIntAsInt() {
		return (int) getVarInt().longValue();
	}
	
	public <T extends ProtocolData> List<T> getVector(Class<T> clazz) {
		try {
			List<T> result = new ArrayList<T>();
			long size = getVarIntAsLong();
			for (long x = 0; x < size; x++) {
				T obj = clazz.newInstance();
				obj.loadFromBuffer(this);
				result.add(obj);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public <T extends ProtocolData> void putVector(List<T> list) {
		try {
			this.putVarInt(list.size());
			for (T t : list) {
				t.writeToBuffer(this);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	public static BigInteger readVarInt(ByteBuffer buf) {
		ByteOrder old = buf.order();
		
		buf.order(ByteOrder.LITTLE_ENDIAN);
		
		int vli = Byte.toUnsignedInt(buf.get());
		
		BigInteger result = null;
		
		if (vli < 0xFD) {
			return BigInteger.valueOf(vli);
		} else if (vli == 0xFD) {
			// 0xFD followed by the length as uint16_t
			char len = buf.getChar();
			return BigInteger.valueOf((long) len);
		} else if (vli == 0xFE) {
			// 0xFE followed by the length as uint32_t
			byte[] arr = new byte[4];
			buf.get(arr);
			ByteUtil.invertArray(arr); // BigInteger requires big endian
			result = new BigInteger(arr);
		} else if (vli == 0xFF) {
			// 0xFF followed by the length as uint64_t
			byte[] arr = new byte[8];
			buf.get(arr);
			ByteUtil.invertArray(arr); // BigInteger requires big endian
			result = new BigInteger(arr);
		} else {
			throw new RuntimeException();
		}
		
		buf.order(old);
		return result;
	}

	
	public static byte[] getVarInt(BigInteger v) {
		if (v.compareTo(BigInteger.valueOf(0xFD)) < 0) {
			return new byte[] { (byte) v.longValue() };
			
		} else if (v.compareTo(BigInteger.valueOf(0xFFFF)) <= 0) {
			ByteBuffer buf = ByteBuffer.allocate(Character.BYTES + 1);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			
			buf.put((byte) 0xFD);
			buf.putChar((char) v.longValue());
			return buf.array();
			
			
		} else if (v.compareTo(BigInteger.valueOf(0xFFFFFFFFL)) <= 0) {
			ByteBuffer buf = ByteBuffer.allocate(5);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put((byte) 0xFE);
			buf.put(ByteUtil.slice(v.toByteArray(), 1, 4));
			
			return buf.array();
		} else {
			ByteBuffer buf = ByteBuffer.allocate(9);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put((byte) 0xFF);
			buf.put(ByteUtil.slice(v.toByteArray(), 1, 8));
			
			return buf.array();
		}
	}
}
