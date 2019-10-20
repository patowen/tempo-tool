package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Dict extends Obj {
	public static final byte type = 0;
	
	private Map<Integer, Obj> dict;
	
	public Dict() {
		dict = new HashMap<>();
	}
	
	public Obj get(int key) throws FileFormatException {
		Obj value = dict.get(key);
		if (value == null) {
			throw new FileFormatException("Dictionary missing required key: " + key);
		}
		return value;
	}
	
	public Obj getOrDefault(int key, Obj defaultValue) {
		Obj value = dict.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
	
	public void set(int key, Obj value) {
		dict.put(key, value);
	}
	
	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeInt(dict.size());
		for (int key : dict.keySet()) {
			stream.writeByte(key);
			dict.get(key).saveObj(stream);
		}
	}
	
	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		int numKeys = stream.readInt();
		for (int i=0; i<numKeys; i++) {
			int key = stream.readInt();
			Obj value = Obj.loadObj(stream);
			dict.put(key, value);
		}
	}
	
	@Override
	public byte getType() {
		return type;
	}
}
