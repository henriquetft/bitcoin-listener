package bitcoinlistener.messages;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;
import bitcoinlistener.datatypes.InvObject;

public class GetDataMessage implements ProtocolMessage {

	private List<InvObject> list = new ArrayList<>();
	
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

	
	public List<InvObject> getList() {
		return list;
	}

	public void setList(List<InvObject> list) {
		this.list = list;
	}

}
