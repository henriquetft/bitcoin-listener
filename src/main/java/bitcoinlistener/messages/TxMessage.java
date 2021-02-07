package bitcoinlistener.messages;

import java.nio.ByteOrder;
import java.util.List;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;
import bitcoinlistener.datatypes.TxIn;
import bitcoinlistener.datatypes.TxOut;
import bitcoinlistener.util.ByteUtil;
import bitcoinlistener.util.HashUtil;

public class TxMessage implements ProtocolMessage {

	private int version; // int32_t
	// flag optional???????
	private List<TxIn> txInList;
	private List<TxOut> txOutList;
	private long lockTime;
	
	private byte[] rawData;

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();

		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);

			int pos = buf.getPosition();
			this.version = buf.getInt32();
			this.txInList = buf.getVector(TxIn.class);
			this.txOutList = buf.getVector(TxOut.class);
			this.lockTime = buf.getUint32();

			int endPos = buf.getPosition();
			buf.setPosition(pos);
			rawData = buf.getBytes(endPos - pos);

		} finally {
			buf.setEndianness(old);
		}
	}

	public String getHash() {
		byte[] data = new byte[rawData.length];
		System.arraycopy(rawData, 0, data, 0, data.length);

		data = HashUtil.sha256(HashUtil.sha256(data));
		ByteUtil.invertArray(data);
		return ByteUtil.bytesToHex(data);
	}

	// @Override
	public void serialize(BitcoinBuffer buf) {
		// FIXME getbytes
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			buf.putInt32(this.version);
			buf.putVector(txInList);
			buf.putVector(txOutList);
			buf.putUint32(lockTime);
		} finally {
			buf.setEndianness(old);
		}
	}

	@Override
	public String getCommand() {
		return "tx";
	}

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("not implemented");
	}

	public List<TxIn> getTxInList() {
		return txInList;
	}

	public List<TxOut> getTxOutList() {
		return txOutList;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [hash=" + getHash() + ", version=" + version +
				", txInList=" + txInList.size() + ", txOutList=" + txOutList.size() +
				", lockTime=" + lockTime + "]";
	}

}
