/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import java.util.Collection;

/**
 * A connection with a bitcoin node.
 */
public interface BitcoinConnection {
	
	/**
	 * Returns whether this connection is open.
	 * 
	 * @return true if this client is connected to a bitcoin node
	 */
	boolean isConnected();
	
	/** Closes this connection */
	void disconnect() throws Exception;
	
	/** Returns the ip address of the bitcoin node */ 
	String getIp();
	
	/** Returns the remote port number to which this client is connected. */
	int getPort();
	
	/** Returns network parameters of the bitcoin network */
	NetworkParameters getNetworkParameters();
	
	/** Sends a message to the bitcoin node */
	void sendMessage(ProtocolMessage msg);
	
	/** Returns a collection of filtered addresses */
	Collection<String> getFilterList();
	
	/**
	 * Sets a bloom filter with all of the addresses in the specified collection.
	 * 
	 * @param address collection containing addresses to be added to the bloom filter
	 */
	void setFilterList(Collection<String> address);

	/**
	 * Sets up a filter config
	 * @param filterConfig Filter configuration
	 */
	void setFilterConfig(FilterConfig filterConfig);

	/**
	 * Returns the current filter config
	 * @return {@link FilterConfig}
	 */
	FilterConfig getFilterConfig();

	/**
	 * Returns the services supported by the peer (received from version Msg).
	 *
	 * @return services encoded as a bitfield
	 * @see {@link bitcoinlistener.ServiceIdentifiers}
	 */
	long getServices();
}
