package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Obj {
	protected abstract void save(DataOutputStream stream) throws IOException;
	
	protected abstract void load(DataInputStream stream) throws IOException, FileFormatException;
	
	public abstract byte getType();
	
	public void saveObj(DataOutputStream stream) throws IOException {
		stream.writeByte(getType());
		save(stream);
	}
	
	public static Obj loadObj(DataInputStream stream) throws IOException, FileFormatException {
		byte type = stream.readByte();
		Obj obj = create(type);
		obj.load(stream);
		return obj;
	}
	
	private static Obj create(byte type) throws FileFormatException {
		switch (type) {
		case Dict.type:
			return new Dict();
		default:
			throw new FileFormatException("Unknown Obj type " + type);
		}
	}
}
