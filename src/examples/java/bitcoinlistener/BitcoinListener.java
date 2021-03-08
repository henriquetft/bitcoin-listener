package bitcoinlistener;

import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.messages.TxMessage;

public class BitcoinListener {

    static {
        String path = BitcoinClient.class.getClassLoader().getResource("logging.properties").getFile();
        System.setProperty("java.util.logging.config.file", path);
    }
    
	public static void main(String[] args) throws Exception {
		
		BitcoinClient c = new BitcoinClient("localhost", 8333, NetworkParameters.TestNet3);
		
		c.addConnectionListener(new ConnectionListener() {
			@Override
			public void event(ConnectionEvent event, BitcoinConnection conn) {
				if (event == ConnectionEvent.Verack) {
					//bitcoind --testnet -debug=net -peerbloomfilters=1
					
					List<String> list = new ArrayList<>();
					list.add("mxq6Fg4ygVU8tdHRvUifPzQFsQJX4XEamF");
					list.add("n4ZdjM5zSU8ujvLz8KkCEzmtnT7uHEVoMV");
					conn.addFilter(list);
				}
			}
		});
		
		c.addTransactionListener(new TransactionListener() {
			@Override
			public void onTransaction(TxMessage tx, BitcoinConnection conn) {
				System.out.println("New Trasaction: " + tx);
			}
		});
		
		c.connect();
	}
}
