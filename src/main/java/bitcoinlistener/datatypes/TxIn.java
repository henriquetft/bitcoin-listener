package bitcoinlistener.datatypes;

import java.nio.ByteOrder;

import bitcoinlistener.BitcoinBuffer;
import bitcoinlistener.ProtocolData;
import bitcoinlistener.util.ByteUtil;

public class TxIn implements ProtocolData {

	private OutPoint previousOutput; // outpoint (36)
	private byte[] signatureScript;  // (var_int + uchar[])
	private long sequence;           // uint32_t
	
	public OutPoint getPreviousOutput() {
		return previousOutput;
	}

	public byte[] getSignatureScript() {
		return signatureScript;
	}

	public long getSequence() {
		return sequence;
	}

	public void setPreviousOutput(OutPoint previousOutput) {
		this.previousOutput = previousOutput;
	}

	public void setSignatureScript(byte[] signatureScript) {
		this.signatureScript = signatureScript;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	@Override
	public void loadFromBuffer(BitcoinBuffer buf) {
		ByteOrder o = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			OutPoint prev = new OutPoint();
			prev.loadFromBuffer(buf);
			int scriptLength = buf.getVarIntAsInt();
			this.signatureScript = buf.getBytes(scriptLength);
			this.sequence = buf.getUint32();
			this.previousOutput = prev;
		} finally {
			buf.setEndianness(o);
		}
	}
	
	@Override
	public void writeToBuffer(BitcoinBuffer buf) {
		ByteOrder old = buf.getEndianness();
		try {
			buf.setEndianness(ByteOrder.LITTLE_ENDIAN);
			previousOutput.writeToBuffer(buf);
			
			buf.putVarInt(this.signatureScript.length);
			buf.putBytes(this.signatureScript);
			buf.putUint32(this.sequence);
		} finally {
			buf.setEndianness(old);
		}
	}

	@Override
	public String toString() {
		return "TxIn [previousOutput=" + previousOutput + ", signatureScript="
				+ ByteUtil.byteArrayToStr(signatureScript) + ", sequence=" + sequence + "]";
	}
}
