/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

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
	 * @param conn {@link bitcoinlistener.BitcoinConnection}
	 */
	void onTransaction(TxMessage tx, BitcoinConnection conn);
}
