/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import bitcoinlistener.messages.MerkleBlockMessage;
import bitcoinlistener.messages.TxMessage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample Usage.
 * <p>
 * Connecting to a bitcoin node and receiving new events.
 */
public class BitcoinListener {

	// Setting up logging system
	static {
		URL logProp = BitcoinClient.class.getClassLoader().getResource(
				"logging.properties");
		if (logProp != null) {
			String path = logProp.getFile();
			System.setProperty("java.util.logging.config.file", path);
		} else {
			System.err.println("logging properties not found");
		}
	}

	private static final String IP = "localhost";
	private static final int PORT = 8333;
	private static final NetworkParameters NETWORK = NetworkParameters.TestNet3;

	// =============================================================================================

	public static void main(String[] args) throws Exception {

		BitcoinClient c = new BitcoinClient(IP, PORT, NETWORK);
		List<String> filtered = new ArrayList<>();

//		// Setting up filters
//		filtered.add("mvQeLdgYBxVi11nE38DM2BkJu7vE161Mqr");
//		filtered.add("tb1qn7xylvtxa6jw9pqc729eqac274sygltcaay8wp");
//		c.setFilterList(filtered);

		// Setting up an observer to receive new transactions
		c.addTransactionListener(new TransactionListener() {
			@Override
			public void onTransaction(TxMessage tx, BitcoinConnection conn) {
				System.out.println("New Trasaction: " + tx);
			}
		});

		// Setting up an observer to receive new blocks
		c.addBlockListener((block, conn) -> {
			System.out.println("New Block: " + block);
			if (block instanceof MerkleBlockMessage) {
				System.out.println("Merkle block. Matched txns: " +
								   ((MerkleBlockMessage) block).getPartialMerkleTree().getMatchedTxIds());
			}
		});

		// Setting up an observer to receive connection events
		c.addConnectionListener(new ConnectionListener() {
			@Override
			public void event(ConnectionEvent event, BitcoinConnection conn) {
				if (event == ConnectionEvent.Disconnected) {
					System.out.println("Disconnected");
				}
			}
		});

		c.connect();
		Thread.sleep(1000);
		System.out.println("Press [enter] to exit");
		System.in.read();
		c.disconnect();
	}
}
