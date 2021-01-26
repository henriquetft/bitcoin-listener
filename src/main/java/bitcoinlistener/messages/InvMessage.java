package bitcoinlistener.messages;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;
import bitcoinlistener.datatypes.InvObject;

/**
 * Allows a node to advertise its knowledge of one or more objects. It can be
 * received unsolicited, or in reply to getblocks.
 */
public class InvMessage implements ProtocolMessage {
	
	private List<InvObject> invObjs = new ArrayList<>();
	
	@Override
	public String getCommand() {
		return "inv";
	}

	@Override
	public byte[] getBytes() {
		throw new RuntimeException("not implemented");
	}
	

	public List<InvObject> getInvObjs() {
		return invObjs;
	}

	public void setInvObjs(List<InvObject> invObjs) {
		this.invObjs = invObjs;
	}

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			this.invObjs = buf.getVector(InvObject.class);
			
		} finally {
			buf.setEndianness(o);
		}
	}

}
