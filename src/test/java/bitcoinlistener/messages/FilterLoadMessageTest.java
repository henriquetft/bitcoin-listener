package bitcoinlistener.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.util.AddressUtil;
import bitcoinlistener.util.BloomFilter;

public class FilterLoadMessageTest {
	
	@Test
	public void testSerializeSimple() {
		BloomFilter f = new BloomFilter(20, 0.1, 2352956L);
		f.insert("abc".getBytes());
		f.insert("abca".getBytes());
		f.insert("oi".getBytes());
		f.insert("abc!".getBytes());
		f.insert("abca!".getBytes());
		f.insert("oi!".getBytes());
		
		FilterLoadMessage m = new FilterLoadMessage();
		m.setFilter(f.getArray());
		m.setNumHashFuncs(f.getNumberOfHashFuncs());
		m.setTweak(f.getNonce());
		m.setFlags((byte) 2); // BLOOM_UPDATE_P2PUBKEY_ONLY
		
		
		byte[] exp = new byte[] {
				11, 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2, 3, 0, 0, 0, 60, -25, 35, 0, 2
		};
		assertArrayEquals(exp, m.getBytes());
	}
	
	@Test
	public void testDeserializeSimple() {
		byte[] exp = new byte[] {
				11, 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2, 3, 0, 0, 0, 60, -25, 35, 0, 2
		};
		
		FilterLoadMessage m = new FilterLoadMessage();
		m.loadFromBuffer(new BitcoinBuffer(exp));
		assertArrayEquals(new byte[] { 64, -112, 0, 3, 17, 64, 37, 4, 0, 36, 2 }, m.getFilter());
		assertEquals(3, m.getNumHashFuncs());
		assertEquals(2352956L, m.getTweak());
	}
	
	
	////////////
	@Test
	public void testSerializeFilter() {
		byte[] exp = new byte[] {
				17, 0, 32, 32, 0, 2, 0, 34, 0, 0, 0, 4, 0, 2, -128, 64, 0, 0, 9, 0, 0, 0, 0, 0, -91, 91, 2
		};
		BloomFilter filter = new BloomFilter(10, 0.001, 3731879741953998848L);
		
		//mxq6Fg4ygVU8tdHRvUifPzQFsQJX4XEamF
		byte[] data = new byte[] { -67, -24, 68, -6, -52, -78, 36, -123, -10, 127, 101, -50, 55, -36, -117, 33, -13, 37, -52, 3 };
		
		filter.insert(data);
		
		FilterLoadMessage filterLoadMessage = new FilterLoadMessage(filter.getArray(),
                filter.getNumberOfHashFuncs(),
                filter.getNonce(),
                (byte) 2); // BLOOM_UPDATE_P2PUBKEY_ONLY
		
		assertArrayEquals(exp, filterLoadMessage.getBytes());
	}
	
	
	@Test
	public void testSerializeFilterTwoAddresses() {
		byte[] exp = new byte[] {
				17, 0, 32, -96, 0, 6, 0, 34, 9, 1, 0, 4, 0, 2, -95, 65, -128, 0, 9, 0, 0, 0, 0, 0, -91, 91, 2
		};
		BloomFilter filter = new BloomFilter(10, 0.001, 3731879741953998848L);
		
		//mxq6Fg4ygVU8tdHRvUifPzQFsQJX4XEamF
		byte[] data = new byte[] { -67, -24, 68, -6, -52, -78, 36, -123, -10, 127, 101, -50, 55, -36, -117, 33, -13, 37, -52, 3 };
		filter.insert(data);
		//n4ZdjM5zSU8ujvLz8KkCEzmtnT7uHEVoMV
		byte[] data2 = new byte[] { -4, -52, -91, 0, -127, 28, -15, -61, -51, -84, 87, -74, -115, 86, 29, -78, 125, 116, 70, -36 };
		filter.insert(data2);
		
		FilterLoadMessage filterLoadMessage = new FilterLoadMessage(filter.getArray(),
                filter.getNumberOfHashFuncs(),
                filter.getNonce(),
                (byte) 2); // BLOOM_UPDATE_P2PUBKEY_ONLY
		
		assertArrayEquals(exp, filterLoadMessage.getBytes());
	}
	
	@Test
	public void testSerializeFilterSeveralAddresses() {
		byte[] exp = new byte[] {
				29, -114, 52, 2, -32, 1, 1, 43, 32, 0, 69, 16, 84, 17, 36, 4, 1, 80, 0, 52, -124, -124, 0, 0, 0, 10, 0, 72, 9, 0, 3, 0, 0, 0, 0, 0, -28, -21, 2
		};
		
		BloomFilter abc = new BloomFilter(50, 0.1, 3721879741953998848L);
		abc.insert(AddressUtil.getAddrHash("n168B6uhamUj4tDSHNzJeGWhvytagKXS2k"));
		abc.insert(AddressUtil.getAddrHash("n1JfXfeafcJKpRbRVQ7FrH3m2D9TWcAPHy"));
		abc.insert(AddressUtil.getAddrHash("mi11rWuB14Eb2L5tpdqfD77DGMhschQdgx"));
		abc.insert(AddressUtil.getAddrHash("mrENFxivvKRDUQaSungyhXFuWCq7hfncPd"));
		abc.insert(AddressUtil.getAddrHash("mxq6Fg4ygVU8tdHRvUifPzQFsQJX4XEamF"));
		abc.insert(AddressUtil.getAddrHash("mz84Ud7WBib8kkCDd7bVyM1HM57hiRjwYQ"));
		abc.insert(AddressUtil.getAddrHash("mfYUXMc9Rb4NcCUj1iLCqwMLPS95VBLSMQ"));
		abc.insert(AddressUtil.getAddrHash("moeR4SWuRovHYid16onoNDzfEZZsM3fcMx"));
		abc.insert(AddressUtil.getAddrHash("mtwmhmx2TC8fjaoHwtKcar9eTk2WjN8BtF"));
		abc.insert(AddressUtil.getAddrHash("mvQeLdgYBxVi11nE38DM2BkJu7vE161Mqr"));
		abc.insert(AddressUtil.getAddrHash("mz4XH5MzLBaEuA8FpeYenGPGXrHx7EADX1"));
		abc.insert(AddressUtil.getAddrHash("mxCofGx6G5BLe8S912sZBiUzZb5iWhXwbe"));
		abc.insert(AddressUtil.getAddrHash("n4ZdjM5zSU8ujvLz8KkCEzmtnT7uHEVoMV"));
		abc.insert(AddressUtil.getAddrHash("n4CWwXNJ9CgM9DyVicnckuewUjGSQwk6rQ"));
		abc.insert(AddressUtil.getAddrHash("mweUsjmG1uPJiraP9f7wmhZYFBYWDnaGB6"));
		abc.insert(AddressUtil.getAddrHash("micrYVxKWQwK61QJrgEhb4onpvDJusJnjG"));
		abc.insert(AddressUtil.getAddrHash("mjGdPxxmaEBY41JhqnfZoA2FV4rJXFz2Mx"));
		abc.insert(AddressUtil.getAddrHash("n4gfEwEsHaRtodLFYGu621nW9KVSHeMkgX"));
		
		FilterLoadMessage filterLoadMessage = new FilterLoadMessage(abc.getArray(),
				abc.getNumberOfHashFuncs(),
				abc.getNonce(),
                (byte) 2); // BLOOM_UPDATE_P2PUBKEY_ONLY
		
		assertArrayEquals(exp, filterLoadMessage.getBytes());
	}

}
