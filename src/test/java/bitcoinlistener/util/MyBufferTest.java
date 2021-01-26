package bitcoinlistener.util;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.nio.ByteOrder;

import org.junit.Test;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.util.MyBuffer;

public class MyBufferTest {

	@Test
	public void testCapacity() {
		MyBuffer buf = new MyBuffer(2);
		buf.setEndianness(ByteOrder.BIG_ENDIAN);
		buf.putByte((byte) 1);
		buf.putByte((byte) 2);
		buf.putInt64(9L);

		// reading data
		buf.setPosition(0);
		assertEquals(buf.getByte(), (byte) 1);
		assertEquals(buf.getByte(), (byte) 2);
		assertEquals(buf.getInt64(), 9L);
	}
	
	@Test
	public void testUint16() {
		BitcoinBuffer b = new BitcoinBuffer(100);
		int x = Character.MAX_VALUE;
		b.putUint16(x);
		b.setPosition(0);
		assertEquals(x, b.getUint16());
	}
	
	@Test
	public void testUint32() {
		BitcoinBuffer b = new BitcoinBuffer(100);
		long c = Integer.MAX_VALUE;
		b.putUint32(c+2);
		b.setPosition(0);
		long k = b.getUint32();
		assertEquals(Integer.MAX_VALUE + 2L, k);
	}
	
	@Test
	public void testUint64() {
		BitcoinBuffer b = new BitcoinBuffer(100);
		BigInteger val = BigInteger.valueOf(Long.MAX_VALUE);
		val = val.add(BigInteger.TEN);
		b.putUint64(val);
		
		b.setPosition(0);
		BigInteger val2 = b.getUint64();
		assertEquals(val, val2);
	}
	
	@Test
	public void testString() {
		BitcoinBuffer b = new BitcoinBuffer(100);
		b.putString("Satoshi Nakamoto", 7);
		
		b.setPosition(0);
		String str = b.getString(7);
		
		assertEquals("Satoshi", str);
	}

}
