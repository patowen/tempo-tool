package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ObjBool extends Obj {
	public static final byte type = 35;
	
	private boolean value;
	
	public ObjBool() {
		this(false);
	}
	
	public ObjBool(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeBoolean(value);
	}

	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		value = stream.readBoolean();
	}

	@Override
	public byte getType() {
		return type;
	}
	
	@Override
	public boolean asBool() throws FileFormatException {
		return value;
	}
}
