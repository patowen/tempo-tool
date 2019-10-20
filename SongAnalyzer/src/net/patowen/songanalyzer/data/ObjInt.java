package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ObjInt extends Obj {
	public static final byte type = 33;
	
	private int value;
	
	public ObjInt() {
		this(0);
	}
	
	public ObjInt(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeInt(value);
	}

	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		value = stream.readInt();
	}

	@Override
	public byte getType() {
		return type;
	}
	
	@Override
	public int asInt() throws FileFormatException {
		return value;
	}
}
