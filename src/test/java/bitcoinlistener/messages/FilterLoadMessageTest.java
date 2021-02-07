package bitcoinlistener.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.util.BloomFilter;

public class FilterLoadMessageTest {
	
	@Test
	public void testSerialize() {
		BloomFilter f = new BloomFilter(20, 0.1, 2352956L);
		f.insert("abc".getBytes());
		f.insert("abca".getBytes());
		f.insert("oi".getBytes());
		f.insert("abc!".getBytes());
		f.insert("abca!".getBytes());
		f.insert("oi!".getBytes());
		
		FilterLoadMessage m = new FilterLoadMessage();
		m.setFilter(f.getArray());
		m.setnHashFuncs(f.getNumberOfHashFuncs());
		m.setnTweak(f.getNonce());
		m.setnFlags((byte) 2); // BLOOM_UPDATE_P2PUBKEY_ONLY
		
		
		byte[] exp = new byte[] {
				11, 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2, 3, 0, 0, 0, 60, -25, 35, 0, 2
		};
		assertArrayEquals(exp, m.getBytes());
	}
	
	@Test
	public void testDeserialize() {
		byte[] exp = new byte[] {
				11, 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2, 3, 0, 0, 0, 60, -25, 35, 0, 2
		};
		
		FilterLoadMessage m = new FilterLoadMessage();
		m.loadFromBuffer(new BitcoinBuffer(exp));
		assertArrayEquals(new byte[] { 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2 }, m.getFilter());
		assertEquals(3, m.getnHashFuncs());
		assertEquals(2352956L, m.getnTweak());
	}
}
