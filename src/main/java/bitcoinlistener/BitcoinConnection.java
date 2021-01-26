package bitcoinlistener;

public interface BitcoinConnection {
	boolean isConnected();
	void disconnect() throws Exception;
	String getIp();
	int getPort();
	NetworkParameters getNetworkParameters();
	void sendMessage(ProtocolMessage msg) throws Exception;
}
