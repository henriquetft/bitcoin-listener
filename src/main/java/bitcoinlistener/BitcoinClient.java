
package bitcoinlistener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitcoinlistener.datatypes.InvObject;
import bitcoinlistener.datatypes.TxIn;
import bitcoinlistener.datatypes.TxOut;
import bitcoinlistener.messages.GetDataMessage;
import bitcoinlistener.messages.InvMessage;
import bitcoinlistener.messages.PingMessage;
import bitcoinlistener.messages.TxMessage;
import bitcoinlistener.messages.Verack;
import bitcoinlistener.messages.VersionMessage;
import bitcoinlistener.util.ByteUtil;


public class BitcoinClient implements BitcoinConnection {

	/*
	 * MAINNET
	 * 2021-01-10T12:16:08Z Loading addresses from DNS seed seed.bitcoin.sprovoost.nl
	2021-01-10T12:16:11Z Loading addresses from DNS seed seed.btc.petertodd.org
	2021-01-10T12:16:11Z Loading addresses from DNS seed seed.bitcoin.jonasschnelli.ch
	2021-01-10T12:16:22Z Loading addresses from DNS seed dnsseed.bluematt.me
	2021-01-10T12:16:23Z Loading addresses from DNS seed dnsseed.emzy.de
	2021-01-10T12:16:25Z Loading addresses from DNS seed seed.bitcoinstats.com

	 */

	// https://en.bitcoin.it/wiki/Protocol_documentation
	// https://developer.bitcoin.org/reference/p2p_networking.html
	// https://www.rapidtables.com/convert/number/hex-to-decimal.html
	
	private static final Logger log = LoggerFactory.getLogger(BitcoinClient.class);
	
	public static final int MY_VERSION = 31800; // Bitcoin Core 0.3.18 (Dec 2010)
	// public static final int MY_VERSION = 70015; // Bitcoin Core 0.13.2 (Jan 2017)
	public static final String MY_SUBVERSION = "bitcoinlistener";

	private BitcoinBuffer buffer = new BitcoinBuffer(1000);
	private int protover = 209;
	private NetworkParameters params;
	private OutputStream out;
	private Socket sock;
	private String ip;
	private int port;
	private List<TransactionListener> txListeners = new ArrayList<TransactionListener>();

	
	private static Map<String, Class<? extends ProtocolMessage>> protocolMessages;
	static {
		protocolMessages = new HashMap<>();
		protocolMessages.put("version", VersionMessage.class);
		protocolMessages.put("inv", InvMessage.class);
		protocolMessages.put("ping", PingMessage.class);
		protocolMessages.put("tx", TxMessage.class);
	}


	public BitcoinClient(String ip, int port, NetworkParameters net) {
		this.ip = ip;
		this.port = port;
		this.params = net;
	}
	
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
		this.sock.close();
	}
	
	@Override
	public boolean isConnected() {
		return this.sock.isConnected();
	}

	
	public void connect() throws Exception {
		log.info("Connecting to node {}:{} ...", ip, port);
		log.info("Network: {}", params.getName());
		this.sock = new Socket(ip, port);
		log.info("Connected successfully!");
		this.out = sock.getOutputStream();

		Thread.sleep(500L);
		new Thread(() -> {
			try {
				log.info("Sending version message ...");
				sendMessage(new VersionMessage(MY_VERSION, MY_SUBVERSION));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).start();

		
		readData();
	}




	public void sendMessage(ProtocolMessage msg) throws Exception {
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
			System.arraycopy(ByteUtil.sha256(ByteUtil.sha256(buf)), 0, checksum, 0, 4);
			buffer.putBytes(checksum);
		}

		buffer.putBytes(buf);

		byte[] arr = buffer.toArrayExactSize();
		
		
		log.debug("+++ Sending message '{}': {}", command, ByteUtil.toHexString(arr));
		this.out.write(arr);
		this.out.flush();
	}
	
	public void addTransactionListener(TransactionListener txListener) {
		txListeners.add(txListener);
	}
	
	
	// =============================================================================================
	// AUXILIARY METHODS
	// =============================================================================================
	
	// magic bytes + command + payloadSize + checkcum
	private static final int HEADER_SIZE = 4 + 12 + 4 + 4;
	
	private void readData() throws Exception {
		ByteArrayOutputStream recvBuf = new ByteArrayOutputStream(1024);
		byte[] buf = new byte[1024];
		InputStream in = sock.getInputStream();
		int i = 0;

		log.info("Starting to read data ...");

		moredata:
		while ((i = in.read(buf)) != -1) {

			log.debug("Bytes read: {}", i);
			log.debug("Data read ({} bytes): {}", i,
					ByteUtil.toHexString(ByteUtil.slice(buf, 0, i)));

			recvBuf.write(buf, 0, i);

			while (true) {
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
		
		
		if (m instanceof VersionMessage) {
			VersionMessage v = (VersionMessage) m;
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
				txListener.onTransaction(tx, this);
			}
		}
	}
}
