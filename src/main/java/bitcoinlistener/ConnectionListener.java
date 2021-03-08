package bitcoinlistener;

public interface ConnectionListener {
	
	public enum ConnectionEvent {
		Connected,   // socket connected
		Verack,      // verack messaged received 
		Disconnected // socket disconnected
	}
	
	void event(ConnectionEvent event, BitcoinConnection conn);
}
