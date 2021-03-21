 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

/**
 * The listener interface for receiving connection events. 
 */
public interface ConnectionListener {
	
	public enum ConnectionEvent {
		/** Socket connected */
		Connected,
		
		/** Socket disconnected */
		Disconnected,
		
		/**
		 * Verack messaged received. Indicates that this connection is ready to send/recieve
		 * other messages
		 */
		Verack,
	}
	
	
	/**
	 * Invoked when an event occurs.
	 * 
	 * @param event Event type of event
	 * @param conn Peer connection
	 */
	void event(ConnectionEvent event, BitcoinConnection conn);
}
