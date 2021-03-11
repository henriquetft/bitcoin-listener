 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

/**
 * It represents data elements used on the protocol messages.
 */
public interface ProtocolData {
	
	/**
	 * Deserialize.
	 * 
	 * @param buf the buffer to read bytes from
	 */
	void loadFromBuffer(BitcoinBuffer buf);
	
	/**
	 * Serialize.
	 * 
	 * @param buf the buffer to write to 
	 */
	void writeToBuffer(BitcoinBuffer buf);
}
