package bitcoinlistener;

import bitcoinlistener.messages.TxMessage;

public class BitcoinListener {

    static {
        String path = BitcoinClient.class.getClassLoader().getResource("logging.properties").getFile();
        System.setProperty("java.util.logging.config.file", path);
    }
    
	public static void main(String[] args) throws Exception {
		
		BitcoinClient c = new BitcoinClient("localhost", 8333, NetworkParameters.TestNet3);
		
		c.addTransactionListener(new TransactionListener() {
			@Override
			public void onTransaction(TxMessage tx, BitcoinConnection conn) {
				System.out.println("New Trasaction: " + tx);
			}
		});
		
		c.connect();
	}
}
