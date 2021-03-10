package bitcoinlistener;

import bitcoinlistener.messages.TxMessage;

/**
 * The listener interface for receiving transaction events. 
 */
public interface TransactionListener {
	
	/**
	 * Invoked when a pair reports a new transaction.
	 * 
	 * @param tx Transaction
	 * @param conn
	 */
	void onTransaction(TxMessage tx, BitcoinConnection conn);
}
