package bitcoinlistener.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A byte buffer to deal with signed and unsigned types
 */
public class MyBuffer {

	protected ByteBuffer buf;

	public MyBuffer(int length) {
		this.buf = ByteBuffer.allocate(length);
	}

	public MyBuffer(byte[] array) {
		this.buf = ByteBuffer.wrap(array);
	}

	public void clear() {
		buf.clear();
	}

	public void rewind() {
		buf.rewind();
	}

	public void putByte(byte b) {
		ensureCapacityFor(Byte.BYTES);
		buf.put(b);
	}

	public void putBytes(byte[] b) {
		ensureCapacityFor(b.length);
		buf.put(b);
	}

	public byte getByte() {
		return buf.get();
	}

	public void putUint32(long value) {
		ensureCapacityFor(Integer.BYTES);
		buf.putInt((int) value);
	}

	public long getUint32() {
		return Integer.toUnsignedLong(buf.getInt());
	}

	public void putInt32(int value) {
		ensureCapacityFor(Integer.BYTES);
		buf.putInt(value);
	}

	public int getInt32() {
		return buf.getInt();
	}

	public int remaining() {
		return buf.remaining();
	}

	public void putUint16(int value) {
		ensureCapacityFor(Character.BYTES);
		buf.putChar((char) value);
	}

	public int getUint16() {
		char value = buf.getChar();
		return (int) value;
	}

	public void putUint64(BigInteger value) {
		ensureCapacityFor(Long.BYTES);
		long c = value.longValue();
		buf.putLong(c);
	}

	public void putUint64(long value) {
		ensureCapacityFor(Long.BYTES);
		buf.putLong(value);
	}

	public BigInteger getUint64() {
		byte[] val = this.getBytes(8);
		if (buf.order() == ByteOrder.LITTLE_ENDIAN) {
			reverse(val);
		}
		return new BigInteger(1, val);
	}

	public void putInt64(long value) {
		ensureCapacityFor(Long.BYTES);
		buf.putLong(value);
	}
	
	
	public boolean getBoolean() {
		byte b = this.getByte();
		return b != (byte) 0;
	}
	
	public void putBoolean(boolean value) {
		ensureCapacityFor(Byte.BYTES);
		buf.put(value ? (byte) 1
		              : (byte) 0);
	}

	public long getInt64() {
		return buf.getLong();
	}

	public byte[] getBytes(int numBytes) {
		byte[] bytes = new byte[numBytes];
		buf.get(bytes);
		return bytes;
	}

	public byte[] toArray() {
		return buf.array();
	}

	public byte[] toArrayExactSize() {
		int oldPos = buf.position();
		byte[] ret = new byte[oldPos];
		try {
			buf.rewind();
			buf.get(ret, 0, ret.length);
		} finally {
			buf.position(oldPos);
		}
		return ret;
	}

	public void setPosition(int newPos) {
		buf.position(newPos);
	}

	public int getPosition() {
		return buf.position();
	}

	public void setEndianness(ByteOrder bo) {
		buf.order(bo);
	}

	public ByteOrder getEndianness() {
		return buf.order();
	}

	public void putString(String str, int length) {
		byte[] arr = new byte[length];
		byte[] bStr = str.getBytes();
		System.arraycopy(bStr, 0, arr, 0, Math.min(length, bStr.length));
		buf.put(arr);
	}

	public String getString(int length) {
		byte[] bytes = getBytes(length);

		int lengthStr = length;
		int pos;
		if ((pos = findByte(bytes, (byte) 0)) != -1) {
			lengthStr = pos;
		}

		String str = new String(bytes, 0, lengthStr);
		return str;
	}

	private static int findByte(byte[] array, byte b) {
		for (int x = 0; x < array.length; x++) {
			if (array[x] == b) {
				return x;
			}
		}
		return -1;
	}

	public static void reverse(byte[] array) {
		for (int x = 0; x < array.length / 2; x++) {
			byte aux = array[x];
			array[x] = array[array.length - x - 1];
			array[array.length - x - 1] = aux;
		}
	}

	private void ensureCapacityFor(int size) {
		if (buf.remaining() <= size) {
			int newSize = Math.max(buf.limit() * 2, size * 2);
			byte[] newArray = new byte[newSize];
			byte[] arr = this.toArrayExactSize();
			System.arraycopy(arr, 0, newArray, 0, arr.length);

			ByteOrder oldByteOrder = buf.order();
			this.buf = ByteBuffer.wrap(newArray);
			this.buf.position(arr.length);
			this.buf.order(oldByteOrder);
		}
	}

}
