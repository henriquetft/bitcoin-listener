/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

/**
 * Base exception for bitcoin-listener
 */
public class BitcoinListenerException extends RuntimeException {

	public BitcoinListenerException() {
		super();
	}

	public BitcoinListenerException(String message) {
		super(message);
	}

	public BitcoinListenerException(Throwable cause) {
		super(cause);
	}
	
	public BitcoinListenerException(String message, Throwable cause) {
		super(message, cause);
	}
}
