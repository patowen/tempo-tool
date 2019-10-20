package net.patowen.songanalyzer.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Arr extends Obj {
	public static final byte type = 1;
	
	private List<Obj> arr;
	
	public Arr() {
		arr = new ArrayList<>();
	}
	
	public Iterator<Obj> getIterator() {
		return arr.iterator();
	}
	
	public void add(Obj value) {
		arr.add(value);
	}
	
	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeInt(arr.size());
		for (Obj value : arr) {
			value.saveObj(stream);
		}
	}
	
	@Override
	protected void load(DataInputStream stream) throws IOException, FileFormatException {
		int numValues = stream.readInt();
		for (int i=0; i<numValues; i++) {
			Obj value = Obj.loadObj(stream);
			arr.add(value);
		}
	}
	
	@Override
	public byte getType() {
		return type;
	}
}