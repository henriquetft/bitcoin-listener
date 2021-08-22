/*
  * Copyright (c) 2021, Henrique Teófilo
  * All rights reserved.
  *
  * This source code is licensed under the BSD-style license found in the
  * LICENSE file in the root directory of this source tree.
  */

 package bitcoinlistener;

 import bitcoinlistener.messages.AbstractBlockMessage;

/**
  * The listener interface for receiving block message.
  */
 public interface BlockListener {

	 /**
	  * Invoked when a pair reports a new block.
	  *  @param block Block
	  * @param conn {@link BitcoinConnection}
	  */
	 void onBlock(AbstractBlockMessage block, BitcoinConnection conn);
 }
