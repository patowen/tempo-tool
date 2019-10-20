package net.patowen.songanalyzer.data.general;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;

public class ObjString extends Obj {
	public static final byte type = 32;
	
	private String value;
	
	public ObjString() {
		this(null);
	}
	
	public ObjString(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeUTF(value);
	}

	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		try {
			value = stream.readUTF();
		} catch (UTFDataFormatException e) {
			throw new FileFormatException("String expected, got something else instead.");
		}
	}

	@Override
	public byte getType() {
		return type;
	}
	
	@Override
	public String asString() throws FileFormatException {
		return value;
	}
}
