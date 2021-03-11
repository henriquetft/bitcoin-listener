 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.datatypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;

public class NetAddress implements ProtocolData {

	// (12 bytes 00 00 00 00 00 00 00 00 00 00 FF FF, followed by the 4 bytes of the IPv4 address).
	private byte[] ipv6 = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0xFF, (byte) 0xFF };

	private long services = 1;
	private String ip = "0.0.0.0";
	private int port = 0;
	
	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================

	public NetAddress() {

	}

	public NetAddress(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	// =============================================================================================
	// OPERATIONS                                                                                
	// =============================================================================================

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {			
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			
			long services = buf.getUint64().longValue(); // 8
			
			buf.setPosition(buf.getPosition() + 12);
			byte[] addr = buf.getBytes(4);
			String ip = InetAddress.getByAddress(addr).getHostAddress();
			
			int port = buf.getUint16();
			
			this.ip = ip;
			this.port = port;
			this.services = services;
			
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} finally {
			buf.setEndianness(old);
		}
		
	}

	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putUint64(services); // uint64_t
			buf.putBytes(ipv6);
			buf.putBytes(InetAddress.getByName(ip).getAddress());

			buf.setEndianness(ByteOrder.BIG_ENDIAN);
			buf.putUint16(port);

		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} finally {
			buf.setEndianness(old);
		}
		
	}

	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " [services=" + services + ", ip=" + ip +
				", port=" + port + "]";
	}
	
}