package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ObjDouble extends Obj {
	public static final byte type = 34;
	
	private double value;
	
	public double getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}

	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeDouble(value);
	}

	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		value = stream.readDouble();
	}

	@Override
	public byte getType() {
		return type;
	}
}
