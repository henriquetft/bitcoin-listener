package bitcoinlistener.messages;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolMessage;

public class Verack implements ProtocolMessage {
	
	public byte[] getBytes() {
		return new byte[0];
	}

	@Override
	public String getCommand() {
		return "verack";
	}

	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		throw new RuntimeException("not implemented");
	}
}
