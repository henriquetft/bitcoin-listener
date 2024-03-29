/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import bitcoinlistener.ConnectionListener.ConnectionEvent;
import bitcoinlistener.datatypes.InvObject;
import bitcoinlistener.datatypes.TxIn;
import bitcoinlistener.datatypes.TxOut;
import bitcoinlistener.messages.*;
import bitcoinlistener.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Concrete class representing and managing a connection to a bitcoin node
 */
public class BitcoinClient implements BitcoinConnection {

	private static final Logger log = LoggerFactory.getLogger(BitcoinClient.class);
	public static final int MY_VERSION = 70015; // Bitcoin Core 0.13.2 (Jan 2017)
	private static final String MY_SUBVERSION = "/bitcoinlistener:0.0.1/";
	public static final double DEFAULT_FALSE_POSITIVE_RATE = 0.0001;
	private static final int HEADER_SIZE = 4 + 12 + 4 + 4; // magic + cmd + payloadSize + checkcum
	private static Map<String, Class<? extends ProtocolMessage>> protocolMessages;

	static {
		// Defines supported protocol messages
		protocolMessages = new HashMap<>();
		protocolMessages.put("version", VersionMessage.class);
		protocolMessages.put("inv", InvMessage.class);
		protocolMessages.put("ping", PingMessage.class);
		protocolMessages.put("tx", TxMessage.class);
		protocolMessages.put("verack", VerackMessage.class);
		protocolMessages.put("block", BlockMessage.class);
		protocolMessages.put("merkleblock", MerkleBlockMessage.class);
	}

	// =============================================================================================

	private BitcoinBuffer buffer = new BitcoinBuffer(1000);
	private int protover = 209;
	private NetworkParameters params;
	private OutputStream out;
	private Socket sock;
	private String ip;
	private int port;
	private BloomFilter filter;
	private List<String> filtered = new ArrayList<>();
	private FilterConfig filterConfig;
	private List<TransactionListener> txListeners = new CopyOnWriteArrayList<>();
	private List<BlockListener> blockListeners = new CopyOnWriteArrayList<>();
	private List<ConnectionListener> connListeners = new CopyOnWriteArrayList<>();

	/**
	 * The services supported by the transmitting node encoded as a bitfield
	 */
	private long services;

	/**
	 * Indicates whether both peers have exchanged their version
	 */
	private volatile boolean verackReceived = false;

	/**
	 * Indicates whether a shutdown for this connection has been requested
	 */
	private volatile boolean shutdownRequested = false;

	private ReentrantLock lock = new ReentrantLock();
	private ReentrantLock filterLock = new ReentrantLock();


	// =============================================================================================
	// CONSTRUCTORS
	// =============================================================================================

	public BitcoinClient(String ip, int port, NetworkParameters net) {
		this.ip = ip;
		this.port = port;
		this.params = net;

		this.filterConfig = new FilterConfig();
		this.filterConfig.setFalsePositiveRate(DEFAULT_FALSE_POSITIVE_RATE);
	}

	// =============================================================================================
	// OPERATIONS
	// =============================================================================================

	public void connect() throws Exception {
		this.shutdownRequested = false;
		this.verackReceived = false;
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
				try {  out.close(); } catch (IOException e) { }
				try { this.sock.close(); } catch (IOException e1) { }
				log.info("Disconnected from {}:{}", ip, port);
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

	public void addBlockListener(BlockListener blockListener) {
		blockListeners.add(blockListener);
	}

	public void addConnectionListener(ConnectionListener connListener) {
		connListeners.add(connListener);
	}

	@Override
	public void setFilterList(Collection<String> addresses) {
		// FIXME implement message filterclear !!!
		filterLock.lock();
		try {
			filtered.clear();
			filtered.addAll(addresses);
		} finally {
			filterLock.unlock();
		}

		if (verackReceived) {
			sendBloomFilter(addresses);
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
		log.debug("Message '{}' received ({}): {}", cmd, message.length,
				  ByteUtil.toHexString(message));

		Class<? extends ProtocolMessage> clazz = protocolMessages.get(cmd);
		if (clazz == null) {
			log.debug("Message '{}' not supported", cmd);
			return;
		}

		ProtocolMessage m = clazz.newInstance();
		m.loadFromBuffer(messageBuffer);

		if (m instanceof VerackMessage) {
			verackReceived = true;
			fireConnectionEvent(ConnectionEvent.Verack);
			sendBloomFilter();

		} else if (m instanceof VersionMessage) {
			VersionMessage v = (VersionMessage) m;
			this.services = v.getServices();
			log.info("Peer: {}", this.getIp());
			log.info("Peer services: {}", this.services);
			log.info("Peer last block received: {}",  v.getStartHeight());
			log.info("Peer Bloom filtering supported: {}", isBloomFilteringSupported());
			this.protover = Math.min(v.getVersion(), MY_VERSION);
			if (this.protover >= 209) {
				sendMessage(new VerackMessage());
			}

		} else if (m instanceof InvMessage) {
			List<InvObject> list = ((InvMessage) m).getInvObjs();
			GetDataMessage getdata = new GetDataMessage();
			for (InvObject invObj : list) {
				log.debug("Inv object received: {}", invObj);
				if (invObj.getType() == InvObject.InventoryType.MSG_TX) {
					getdata.addObject(invObj);
				} else if (invObj.getType() == InvObject.InventoryType.MSG_BLOCK) {
					if (hasFilter()) {
						invObj.setType(InvObject.InventoryType.MSG_FILTERED_BLOCK);
					}
					getdata.addObject(invObj);
				}
			}
			if (getdata.hasObjects()) {
				sendMessage(getdata);
			}

		} else if (m instanceof PingMessage) {
			PingMessage ping = (PingMessage) m;
			if (ping.hasNonce()) {
				sendMessage(new PongMessage(ping.getNonce()));
			}

		} else if (m instanceof TxMessage) {
			TxMessage tx = (TxMessage) m;
			log.info("---------------------------------------------------------------------------");
			log.info("Transaction received {}", tx.getHash());
			log.info(tx.toString());
			for (TxIn in : tx.getTxInList()) {
				log.info(in.toString());
			}
			for (TxOut out : tx.getTxOutList()) {
				log.info(out.toString());
			}
			log.info("---------------------------------------------------------------------------");
			fireTransactionEvent(tx);

		} else if (m instanceof BlockMessage) {
			BlockMessage block = (BlockMessage) m;
			log.info("---------------------------------------------------------------------------");
			log.info("Block received {}", block.getHashAsStr());
			log.info(block.toString());
			log.info("---------------------------------------------------------------------------");
			fireBlockEvent(block);

		} else if (m instanceof MerkleBlockMessage) {
			MerkleBlockMessage block = (MerkleBlockMessage) m;
			log.info("---------------------------------------------------------------------------");
			log.info("Merkle Block received {}", block.getHashAsStr());
			log.info(block.toString());
			log.info("---------------------------------------------------------------------------");
			PartialMerkleTree pmt = new PartialMerkleTree(block.getHashes(),
														  block.getTotalTransactions(),
														  ByteUtil.getFlagList(block.getFlags()));
			try {
				pmt.build();
				if (!pmt.getMerkleRoot().equals(block.getMerkleRoot())) {
					throw new RuntimeException("Computed merkle root not equals to block header");
				}
				log.info("Matched txns: " + pmt.getMatchedTxIds());
			} catch (Exception ex) {
				ex.printStackTrace();
				log.warn("MerkleBlock is invalid: " + ex.getMessage());
			}
			block.setPartialMerkleTree(pmt);
			fireBlockEvent(block);
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

	private void fireBlockEvent(AbstractBlockMessage block) {
		for (BlockListener blockListener : blockListeners) {
			try {
				blockListener.onBlock(block, this);
			} catch (Throwable t) {
				log.warn("Error calling block listener", t);
			}
		}
	}

	private void fireTransactionEvent(TxMessage tx) {
		for (TransactionListener txListener : txListeners) {
			try {
				txListener.onTransaction(tx, this);
			} catch (Throwable t) {
				log.warn("Error calling transaction listener", t);
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
