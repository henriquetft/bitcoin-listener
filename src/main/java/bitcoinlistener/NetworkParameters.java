/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener;

/**
 * Enum with all supported networks and its parameters.
 */
public enum NetworkParameters {
	
	MainNet("MAINNET", new byte[] { (byte) 0xF9, (byte) 0XBE, (byte) 0XB4, (byte) 0xD9 }),
	TestNet3("TESTNET3", new byte[] { (byte) 0x0B, (byte) 0x11, (byte) 0x09, (byte) 0x07 }),
	RegTest("REGTEST", new byte[] { (byte) 0xFA, (byte) 0xBF, (byte) 0xB5, (byte) 0xDA });

	/** Name of this network */
	private String name;

	/** Magic value indicating message origin network */
	private byte[] magicValue;

	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	NetworkParameters(String name, byte[] magicValue) {
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
