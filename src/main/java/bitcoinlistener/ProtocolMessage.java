package bitcoinlistener;

public interface ProtocolMessage {
	String getCommand();
	byte[] getBytes();
	void loadFromBuffer(BitcoinBuffer buf);
}
