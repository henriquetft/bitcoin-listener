package bitcoinlistener;

import bitcoinlistener.messages.TxMessage;

public interface TransactionListener {
	void onTransaction(TxMessage tx, BitcoinConnection conn);
}
