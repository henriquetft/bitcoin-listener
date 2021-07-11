/*
 * Copyright (c) 2021, Henrique Teófilo
 * All rights reserved.
 * 
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package bitcoinlistener.messages;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;
import bitcoinlistener.datatypes.InvObject;

/**
 * Getdata is used in response to inv, to retrieve the content of a specific
 * object. It can be used to retrieve transactions (in mempool)
 */
public class GetDataMessage implements ProtocolMessage {

	private List<InvObject> list = new ArrayList<>();
	
	// =============================================================================================
	// CONSTRUCTORS                                                                                
	// =============================================================================================
	
	public GetDataMessage() {
		
	}
	
	// =============================================================================================
	// OPERATIONS                                                            
	// =============================================================================================
	
	@Override
	public String getCommand() {
		return "getdata";
	}

	@Override
	public byte[] getBytes() {
		BitcoinBuffer buf = new BitcoinBuffer(36 * list.size() + 9);
		buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
		buf.putVector(list);
		byte[] arr = buf.toArrayExactSize();
		return arr;
	}
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		throw new RuntimeException("not implemented");
	}

	public void addObject(InvObject obj) {
		list.add(obj);
	}
	
	public boolean hasObjects() {
		return !list.isEmpty();
	}

	// =============================================================================================
	// ACCESSORS (GETTERS AND SETTERS)                                                              
	// =============================================================================================
	
	public List<InvObject> getList() {
		return list;
	}

	public void setList(List<InvObject> list) {
		this.list = list;
	}

}
