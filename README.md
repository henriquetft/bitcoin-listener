# bitcoin-listener

A simple and minimalist Bitcoin blockchain listener.

It connects directly to a bitcoin node and listen for events (e.g. new transactions)

This project does **not** require any external dependencies for connecting to the bitcoin network.


## Sample Usage
```java

BitcoinClient c = new BitcoinClient("localhost", 8333, NetworkParameters.TestNet3);
		
c.addTransactionListener(new TransactionListener() {
	@Override
	public void onTransaction(TxMessage tx, BitcoinConnection conn) {
		System.out.println("New Trasaction: " + tx);
	}
});

c.addBlockListener((block, conn) -> {
    System.out.println("New Block: " + block);
});

c.connect();
```