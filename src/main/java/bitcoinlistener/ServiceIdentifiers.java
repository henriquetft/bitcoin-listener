 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

// https://developer.bitcoin.org/reference/p2p_networking.html
public class ServiceIdentifiers {

	/**
	 * This node is not a full node. It may not be able to provide any data except
	 * for the transactions it originates.
	 */
	public static final int Unnamed = 0x00;

	/**
	 * This is a full node and can be asked for full blocks. It should implement all
	 * protocol features available in its self-reported protocol version.
	 */
	public static final int NODE_NETWORK = 0x01;

	/**
	 * This is a full node capable of responding to the getutxo protocol request.
	 * This is not supported by any currently-maintained Bitcoin node. See BIP64 for
	 * details on how this is implemented
	 */
	public static final int NODE_GETUTXO = 0x02;

	/**
	 * This is a full node capable and willing to handle bloom-filtered connections.
	 * See BIP111 for details.
	 */
	public static final int NODE_BLOOM = 0x04;

	/**
	 * This is a full node that can be asked for blocks and transactions including
	 * witness data. See BIP144 for details.
	 */
	public static final int NODE_WITNESS = 0x08;

	/**
	 * This is a full node that supports Xtreme Thinblocks. This is not supported by
	 * any currently-maintained Bitcoin node.
	 */
	public static final int NODE_XTHIN = 0x10;

	/**
	 * This is the same as NODE_NETWORK but the node has at least the last 288
	 * blocks (last 2 days). See BIP159 for details on how this is implemented.
	 */
	public static final int NODE_NETWORK_LIMITED = 0x0400;

}
