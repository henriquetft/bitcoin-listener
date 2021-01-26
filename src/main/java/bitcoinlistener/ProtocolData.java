package bitcoinlistener;

public interface ProtocolData {
	void loadFromBuffer(BitcoinBuffer buf);
	void writeToBuffer(BitcoinBuffer buf);
}
