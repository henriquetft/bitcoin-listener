 /*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

public enum NetworkParameters {
	
	MainNet("MAINNET", new byte[] { (byte) 0xF9, (byte) 0XBE, (byte) 0XB4, (byte) 0xD9 }),
	TestNet3("TESTNET3", new byte[] { (byte) 0x0B, (byte) 0x11, (byte) 0x09, (byte) 0x07 });
	
	
	private String name;
	private byte[] magicValue;

	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	private NetworkParameters(String name, byte[] magicValue) {
		this.name = name;
		this.magicValue = magicValue;
	}
	
	
	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                             
	// =============================================================================================
	
	
	public String getName() {
		return name;
	}

	public byte[] getMagicValue() {
		return magicValue;
	}

	
	// =============================================================================================
	// OBJECT OPERATIONS                                                                           
	// =============================================================================================
	
	public String toString() {
		return this.name;
	}
}
