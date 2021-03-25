 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitcoinlistener.ConnectionListener.ConnectionEvent;
import bitcoinlistener.datatypes.InvObject;
import bitcoinlistener.datatypes.TxIn;
import bitcoinlistener.datatypes.TxOut;
import bitcoinlistener.messages.FilterLoadMessage;
import bitcoinlistener.messages.GetDataMessage;
import bitcoinlistener.messages.InvMessage;
import bitcoinlistener.messages.PingMessage;
import bitcoinlistener.messages.TxMessage;
import bitcoinlistener.messages.Verack;
import bitcoinlistener.messages.VersionMessage;
import bitcoinlistener.util.AddressUtil;
import bitcoinlistener.util.BloomFilter;
import bitcoinlistener.util.ByteUtil;
import bitcoinlistener.util.HashUtil;

/**
 * Concrete class representing and managing a connection to a bitcoin node
 */
public class BitcoinClient implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BitcoinClient.class);
	public static final int MY_VERSION = 70015; // Bitcoin Core 0.13.2 (Jan 2017)
	private static final String MY_SUBVERSION = "/bitcoinlistener:0.0.1/";
	// magicbytes + cmd + payloadSize + checkcum
	private static final int HEADER_SIZE = 4 + 12 + 4 + 4;
	private static Map<String, Class<? extends ProtocolMessage>> protocolMessages;
	
	static {
		protocolMessages = new HashMap<>();
		protocolMessages.put("version", VersionMessage.class);
		protocolMessages.put("inv", InvMessage.class);
		protocolMessages.put("ping", PingMessage.class);
		protocolMessages.put("tx", TxMessage.class);
		protocolMessages.put("verack", Verack.class);
	}

	// =============================================================================================
	
	private BitcoinBuffer buffer = new BitcoinBuffer(1000);
	private int protover = 209;
	private NetworkParameters params;
	private OutputStream out;
	private Socket sock;
	private String ip;
	private int port;
	private List<TransactionListener> txListeners = new CopyOnWriteArrayList<>();
	private List<ConnectionListener> connListeners = new CopyOnWriteArrayList<>();
	private BloomFilter filter;
	private List<String> filtered = new ArrayList<>();
	private FilterConfig filterConfig;
	
	/** The services supported by the transmitting node encoded as a bitfield */
	private long services;
	
	private volatile boolean verackReceived;
	
	private ReentrantLock lock = new ReentrantLock();
	private ReentrantLock filterLock = new ReentrantLock();
	private volatile boolean shutdownRequested = false;

	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================

	public BitcoinClient(String ip, int port, NetworkParameters net) {
		this.ip = ip;
		this.port = port;
		this.params = net;
		
		this.filterConfig = new FilterConfig();
		this.filterConfig.setFalsePositiveRate(0.0001);
	}
	
	
	// =============================================================================================
	// OPERATIONS                                                                                   
	// =============================================================================================
	
	public void connect() throws Exception {
		this.shutdownRequested = false;
		lock.lock();
		try {
			log.info("Connecting to node {}:{} ...", ip, port);
			log.info("Network: {}", params.getName());
			this.sock = new Socket(ip, port);
			log.info("Connected successfully!");
			fireConnectionEvent(ConnectionListener.ConnectionEvent.Connected);
			this.out = sock.getOutputStream();
		} finally {
			lock.unlock();
		}
		

		// Start reading loop on another thread
		new Thread(() -> {
			try {
				readData();
			} catch (SocketException e) {
				if (!shutdownRequested) {
					log.error("Error reading data", e);
				}
			} catch (Exception e) {
				log.error("Error reading data", e);
			} finally {
				try { out.close(); } catch (IOException e) { }
				try { this.sock.close(); } catch (IOException e1) { }
				fireConnectionEvent(ConnectionEvent.Disconnected);
			}
		}).start();
		
		Thread.sleep(500L);
		
		filterLock.lock();
		try {
			// Send relay=false when filtering
			boolean filtering = hasFilter();
			log.info("Sending version message ...");
			sendMessage(new VersionMessage(MY_VERSION, MY_SUBVERSION, !filtering));
		} catch (Exception e) {
			throw new BitcoinListenerException("Error sending version", e);
		} finally {
			filterLock.unlock();
		}
	}


	public void sendMessage(ProtocolMessage msg) {
		/*
		 * Message structure
		 * Field Size 	Description 	Data type 	
		 * 4             magic          uint32_t
		 * 12            command        char[12]
		 * 4             length         uint32_t
		 * 4             checksum       uint32_t
		 * ?             payload        uchar[] 
		 */

		String command = msg.getCommand();
		buffer.clear();
		
		buffer.setEndianness(ByteOrder.BIG_ENDIAN);

		buffer.putBytes(params.getMagicValue());
		buffer.putBytes(command.getBytes());
		int zeros = 12 - command.length();
		for (int x = 0; x < zeros; x++) {
			buffer.putByte((byte) 0);
		}

		byte[] buf = msg.getBytes();

		buffer.setEndianness(ByteOrder.LITTLE_ENDIAN);
		buffer.putUint32(buf.length);
		buffer.setEndianness(ByteOrder.BIG_ENDIAN);

		if (this.protover >= 209) {
			byte[] checksum = new byte[4];
			System.arraycopy(HashUtil.sha256(HashUtil.sha256(buf)), 0, checksum, 0, 4);
			buffer.putBytes(checksum);
		}

		buffer.putBytes(buf);

		byte[] arr = buffer.toArrayExactSize();
		
		lock.lock();
		try {
			log.debug("+++ Sending message '{}': {}", command, ByteUtil.toHexString(arr));
			try {
				this.out.write(arr);
				this.out.flush();
			} catch (IOException e) {
				throw new BitcoinListenerException("Error sending message to socket", e);
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void addTransactionListener(TransactionListener txListener) {
		txListeners.add(txListener);
	}
	
	public void addConnectionListener(ConnectionListener connListener) {
		connListeners.add(connListener);
	}
	
	

	@Override
	public void setFilterList(Collection<String> addresses) {
		Collection<String> list = addresses;
		
		filterLock.lock();
		try {
			filtered.clear();
			filtered.addAll(addresses);
		} finally {
			filterLock.unlock();
		}
		
		if (verackReceived) {
			sendBloomFilter(list);
		}
	}
	
	public Collection<String> getFilterList() {
		filterLock.lock();
		try {
			return Collections.unmodifiableList(filtered);
		} finally {
			filterLock.unlock();
		}
	}
	
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                             
	// =============================================================================================
	
	@Override
	public NetworkParameters getNetworkParameters() {
		return this.params;
	}

	@Override
	public String getIp() {
		return this.ip;
	}

	@Override
	public int getPort() {
		return this.port;
	}
	
	@Override
	public void disconnect() throws Exception {
		lock.lock();
		try {
			this.shutdownRequested = true;
			this.sock.close();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isConnected() {
		lock.lock();
		try {
			return this.sock.isConnected();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void setFilterConfig(FilterConfig filterConfig) {
		filterLock.lock();
		try {
			this.filterConfig = filterConfig;
		} finally {
			filterLock.unlock();
		}
		
	}

	@Override
	public FilterConfig getFilterConfig() {
		filterLock.lock();
		try {
			return this.filterConfig;
		} finally {
			filterLock.unlock();
		}
	}

	@Override
	public long getServices() {
		return this.services;
	}
	
	
	// =============================================================================================
	// AUXILIARY METHODS
	// =============================================================================================
	
	
	private void readData() throws Exception {
		ByteArrayOutputStream recvBuf = new ByteArrayOutputStream(1024);
		byte[] buf = new byte[1024];
		InputStream in = sock.getInputStream();
		int i = 0;

		log.info("Starting to read data ...");

		moredata:
		while ((i = in.read(buf)) != -1 && !shutdownRequested) {

			log.debug("Bytes read: {}", i);
			log.debug("Data read ({} bytes): {}", i,
					ByteUtil.toHexString(ByteUtil.slice(buf, 0, i)));

			recvBuf.write(buf, 0, i);

			while (!shutdownRequested) {
				int pos = 0;
				
				if (recvBuf.size() < HEADER_SIZE) {
					log.debug("Awaiting more data ...");
					continue moredata;
				}

				byte[] arr = recvBuf.toByteArray();

				// MAGIC BYTES (4)
				byte[] magicValue = params.getMagicValue();
				if (!ByteUtil.compareArray(arr, 0, magicValue, magicValue.length)) {
					log.error("MAGIC BYTES ERROR: {}", ByteUtil.toHexString(arr));
					throw new RuntimeException("Magic bytes doens't match!");
				}
				pos += params.getMagicValue().length; // 4

				byte[] command = ByteUtil.slice(arr, pos, 12); // COMMAND (12)

				String cmd = new String(command, 0, ByteUtil.findByte(command, (byte) 0));
				log.debug("Command received: {}", cmd);
				pos += 12;

				// PAYLOAD SIZE (4)
				int payloadSize = ByteUtil.intFromBytes(ByteUtil.slice(arr, pos, 4));
				log.debug("Payload size: {} ", payloadSize);
				pos += 4;

				// CHECKSUM (4)
				// FIXME verify checksum
				pos += 4;

				if (pos + payloadSize > arr.length) {
					log.debug("Awaiting more data ...");
					continue moredata;
				}

				byte[] message = ByteUtil.slice(arr, pos, payloadSize);
				onMessageReceived(cmd, message);
				pos += payloadSize;

				// put the remaining bytes on recvBuf
				recvBuf.reset();
				recvBuf.write(arr, pos, arr.length - pos);
				log.debug("Remaining bytes to be read: {}",
						ByteUtil.toHexString(recvBuf.toByteArray()));
			}
		}
	}

	
	private void onMessageReceived(String cmd, byte[] message) throws Exception {
		BitcoinBuffer messageBuffer = new BitcoinBuffer(message);
		log.debug("Message received ({}): {}", message.length, ByteUtil.toHexString(message));
		
		Class<? extends ProtocolMessage> clazz = protocolMessages.get(cmd);
		if (clazz == null) {
			log.debug("Message '{}' not supported", cmd);
			return;
		}
		
		ProtocolMessage m = clazz.newInstance();
		m.loadFromBuffer(messageBuffer);
		
		if (m instanceof Verack) {
			verackReceived = true;
			fireConnectionEvent(ConnectionEvent.Verack);
			sendBloomFilter();
			
		} else if (m instanceof VersionMessage) {
			VersionMessage v = (VersionMessage) m;
			this.services = v.getServices();
			log.info("Peer services: {}", this.services);
			log.info("Bloom filtering supported: {}", isBloomFilteringSupported());
			this.protover = Math.min(v.getVersion(), MY_VERSION);
			if (this.protover >= 209) {
				sendMessage(new Verack());
			}

		} else if (m instanceof InvMessage) {
			List<InvObject> list = ((InvMessage) m).getInvObjs();
			GetDataMessage getdata = new GetDataMessage();
			for (InvObject invObj : list) {
				if (invObj.getType() == 1) {
					getdata.addObject(invObj);
				} else if (invObj.getType() == 2) {
					getdata.addObject(invObj);
				}
			}
			if (getdata.hasObjects()) {
				sendMessage(getdata);
			}
			
		} else if (m instanceof PingMessage) {
			PingMessage ping = (PingMessage) m;
			if (ping.hasNonce()) {
				// send pong?
			}
			
		} else if (m instanceof TxMessage) {
			TxMessage tx = (TxMessage) m;
			log.info("---------------------------------------------------------------------------");
			log.info("Transaction received {}", tx.getHash());
			log.info(tx.toString());
			for (TxIn in: tx.getTxInList()) {
				log.info(in.toString());
			}
			for (TxOut out: tx.getTxOutList()) {
				log.info(out.toString());
			}
			log.info("---------------------------------------------------------------------------");
			for (TransactionListener txListener : txListeners) {
				try {
					txListener.onTransaction(tx, this);
				} catch (Throwable t) {
					log.warn("Error calling transaction listener", t);
				}
			}
		}
	}
	
	// =============================================================================================
	
	private void fireConnectionEvent(ConnectionEvent event) {
		for (ConnectionListener connListener : connListeners) {
			try {
				connListener.event(event, this);
			} catch (Throwable t) {
				log.warn("Error calling connection listener", t);
			}
		}
	}
	
	// =============================================================================================
	
	private boolean hasFilter() {
		filterLock.lock();
		try {
			return (filtered != null && !filtered.isEmpty());
		} finally {
			filterLock.unlock();
		}
	}
	
	private boolean isBloomFilteringSupported() {
		return ((ServiceIdentifiers.NODE_BLOOM & services) == ServiceIdentifiers.NODE_BLOOM);
	}
	
	private void sendBloomFilter() {
		sendBloomFilter(filtered);
	}

	private void sendBloomFilter(Collection<String> addressList) {

		if (addressList.isEmpty()) {
			return;
		}
		
		if (!isBloomFilteringSupported()) {
			throw new BitcoinListenerException("Filtering not supported by peer");
		}
		
		this.filter = new BloomFilter(addressList.size() * 2,
		                              filterConfig.getFalsePositiveRate(),
		                              System.currentTimeMillis());
		
		for (String addr : addressList) {
			byte[] bytes = AddressUtil.getAddrHash(addr);
			
			this.filter.insert(bytes);
			log.debug("Inserting {} to bloom filter", Arrays.toString(bytes));
		}

		
		FilterLoadMessage filterLoadMessage = new FilterLoadMessage(filter.getAsArray(),
		                                                            filter.getNumberOfHashFuncs(),
		                                                            filter.getNonce(),
		                                                            // BLOOM_UPDATE_P2PUBKEY_ONLY
		                                                            (byte) 2);

		sendMessage(filterLoadMessage);
		
	}

}
