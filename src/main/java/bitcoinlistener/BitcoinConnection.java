 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import java.util.List;

/**
 * A connection with a bitcoin node.
 *  
 */
public interface BitcoinConnection {
	
	/** Returns whether this connection is closed */
	boolean isConnected();
	
	/** Closes this connection */
	void disconnect() throws Exception;
	
	String getIp();
	int getPort();
	NetworkParameters getNetworkParameters();
	void sendMessage(ProtocolMessage msg);
	
	void addFilter(List<String> address);
	void addFilter(String address);
	void setFilterConfig(FilterConfig filterConfig);
	FilterConfig getFilterConfig();
	
	/** Returns the services supported by the transmitting node encoded as a bitfield. */
	long getServices();
}
