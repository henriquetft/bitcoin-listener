 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.messages.TxMessage;

/**
 * Sample Usage.
 * 
 * Connecting to a bitcoin node and receiving new events.
 *
 */
public class BitcoinListener {

	// Setting up logging system
    static {
        String path = BitcoinClient.class.getClassLoader().getResource("logging.properties").getFile();
        System.setProperty("java.util.logging.config.file", path);
    }
    
    private static final String IP = "localhost";
    private static final int PORT = 8333;
    private static final NetworkParameters NETWORK = NetworkParameters.TestNet3;
    
    
	public static void main(String[] args) throws Exception {
		
		BitcoinClient c = new BitcoinClient(IP, PORT, NETWORK);
		
		c.addConnectionListener(new ConnectionListener() {
			@Override
			public void event(ConnectionEvent event, BitcoinConnection conn) {
				
				// After connection is open and both peers have exchanged their version
				if (event == ConnectionEvent.Verack) {
					List<String> list = new ArrayList<>();
					list.add("muBB8CVDwX3ujEZ9LgahA3ebx38buaJp1Y");
					conn.addFilter(list);
				}
			}
		});
		
		// Setting up an observer to recieve new transactions
		c.addTransactionListener(new TransactionListener() {
			@Override
			public void onTransaction(TxMessage tx, BitcoinConnection conn) {
				System.out.println("New Trasaction: " + tx);
			}
		});
		
		c.connect();
	}
}
