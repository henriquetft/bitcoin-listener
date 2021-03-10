package bitcoinlistener;

/**
 * The listener interface for receiving connection events. 
 */
public interface ConnectionListener {
	
	public enum ConnectionEvent {
		/** Socket connected */
		Connected,
		
		/** Socket disconnected */
		Disconnected, // socket disconnected
		
		/**
		 * Verack messaged received. Indicates that this connection is ready to send/recieve
		 * other messages
		 */
		Verack,
	}
	
	
	/**
	 * Invoked when an event occurs.
	 * 
	 * @param event Event
	 * @param conn Peer connection
	 */
	void event(ConnectionEvent event, BitcoinConnection conn);
}
