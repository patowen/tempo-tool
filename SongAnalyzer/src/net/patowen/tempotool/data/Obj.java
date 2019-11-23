package net.patowen.tempotool.data;

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
		case Arr.type:
			return new Arr();
		case ObjString.type:
			return new ObjString();
		case ObjInt.type:
			return new ObjInt();
		case ObjDouble.type:
			return new ObjDouble();
		case ObjBool.type:
			return new ObjBool();
		default:
			throw new FileFormatException("Unknown Obj type " + type);
		}
	}
	
	public Arr asArr() throws FileFormatException {
		throw new FileFormatException("Arr expected, got " + getType());
	}
	
	public Dict asDict() throws FileFormatException {
		throw new FileFormatException("Dict expected, got " + getType());
	}
	
	public String asString() throws FileFormatException {
		throw new FileFormatException("ObjString expected, got " + getType());
	}
	
	public int asInt() throws FileFormatException {
		throw new FileFormatException("ObjInt expected, got " + getType());
	}
	
	public double asDouble() throws FileFormatException {
		throw new FileFormatException("ObjDouble expected, got " + getType());
	}
	
	public boolean asBool() throws FileFormatException {
		throw new FileFormatException("ObjBool expected, got " + getType());
	}
}
