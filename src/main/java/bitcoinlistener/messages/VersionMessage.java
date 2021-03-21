 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;
import bitcoinlistener.datatypes.NetAddress;
import bitcoinlistener.datatypes.VarString;
import bitcoinlistener.util.ByteUtil;

/**
 * Version Message.
 * 
 * When a node creates an outgoing connection, it will immediately advertise its version.
 */
public class VersionMessage implements ProtocolMessage {

	private int version;           // int32_t   (4)
	private long services;         // uint64_t  (8)
	private long time;             // int64_t   (8)
	private NetAddress addrRecv;   // net_addr  (26)
	private NetAddress addrFrom;   // net_addr  (26)
	private long nonce;            // uint64_t  (8) 
	private String subVer;         // VarString (?)
	private int startHeight;       // int32_t   (4)
	private boolean relay;         // bool      (1)
	
	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public VersionMessage() {
		
	}

	public VersionMessage(int version, long services, long time, NetAddress addrRecv,
			NetAddress addrFrom, long nonce, String subVer, int startHeight, boolean relay) {
		super();
		this.version = version;
		this.services = services;
		this.time = time;
		this.addrRecv = addrRecv;
		this.addrFrom = addrFrom;
		this.nonce = nonce;
		this.subVer = subVer;
		this.startHeight = startHeight;
		this.relay = relay;
	}

	
	public VersionMessage(int version, String subVer, boolean relay) {
		this.version = version;
		this.services = 0;
		this.time = System.currentTimeMillis() / 1000L; // unix timestamp
		this.addrRecv = new NetAddress("127.0.0.1", 8333);
		this.addrFrom = new NetAddress();
		this.nonce = ByteUtil.getRandomNonce();
		this.subVer = subVer;
		this.startHeight = -1;
		this.relay = relay;
	}

	
	// =============================================================================================
	/// OPERATIONS                                                                                
	// =============================================================================================
	
	@Override
	public String getCommand() {
		return "version";
	}


	public byte[] getBytes() {
		BitcoinBuffer buf = new BitcoinBuffer(10000);
		buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
		buf.putInt32(this.version);                     // 4
		buf.putUint64(this.services);                   // 8
		buf.putInt64(this.time);                        // 8
		buf.putData(this.addrRecv);                     // 26
		buf.putData(this.addrFrom);                     // 26
		buf.putUint64(this.nonce);                      // 8
		
		VarString v = new VarString(this.subVer);
		v.writeToBuffer(buf);
		
		buf.putInt32(this.startHeight);                 // 4

		if (version > 70001) {
			buf.putBoolean(this.relay);                 // 1
		}
		
		byte[] ret = buf.toArrayExactSize();

		return ret;
	}
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			
			int version = buf.getInt32();
			long services = buf.getUint64().longValue();
			long time = buf.getInt64();

			NetAddress addrRecv = new NetAddress();
			addrRecv.loadFromBuffer(buf);

			
			NetAddress addrFrom = new NetAddress();
			addrFrom.loadFromBuffer(buf);

			long nonce = buf.getUint64().longValue();

			
			VarString vStr = new VarString();
			vStr.loadFromBuffer(buf);
			String str = vStr.getString();


			int height = -1;
			if (version >= 209) {
				height = buf.getInt32();
			}

			// Transaction relay flag
			boolean relay = true;
			if (version > 70001) {
				relay = buf.getBoolean();
			}

			this.version = version;
			this.services = services;
			this.time = time;
			this.addrRecv = addrRecv;
			this.addrFrom = addrFrom;
			this.nonce = nonce;
			this.subVer = str;
			this.startHeight = height;
			this.relay = relay;

		} finally {
			buf.setEndianness(o);
		}
	}
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getServices() {
		return services;
	}

	public void setServices(long services) {
		this.services = services;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public NetAddress getAddrRecv() {
		return addrRecv;
	}

	public void setAddrRecv(NetAddress addrRecv) {
		this.addrRecv = addrRecv;
	}

	public NetAddress getAddrFrom() {
		return addrFrom;
	}

	public void setAddrFrom(NetAddress addrFrom) {
		this.addrFrom = addrFrom;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String getSubVer() {
		return subVer;
	}

	public void setSubVer(String subVer) {
		this.subVer = subVer;
	}

	public int getStartHeight() {
		return startHeight;
	}

	public void setStartHeight(int startHeight) {
		this.startHeight = startHeight;
	}

	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[version=" + version + ", services=" + services + 
				", time=" + time + ", addrRecv=" + addrRecv + ", addrFrom=" + addrFrom + 
				", nonce=" + nonce + ", subVer=" + subVer + ", startHeight=" + startHeight + 
				", relay=" + relay + "]";
	}
}