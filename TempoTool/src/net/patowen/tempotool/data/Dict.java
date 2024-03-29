/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.patowen.tempotool.data;

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
	
	public void set(int key, String value) {
		dict.put(key, new ObjString(value));
	}
	
	public void set(int key, int value) {
		dict.put(key, new ObjInt(value));
	}
	
	public void set(int key, double value) {
		dict.put(key, new ObjDouble(value));
	}
	
	public void set(int key, boolean value) {
		dict.put(key, new ObjBool(value));
	}
	
	@Override
	protected void save(DataOutputStream stream) throws IOException {
		stream.writeInt(dict.size());
		for (int key : dict.keySet()) {
			stream.writeInt(key);
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
	
	@Override
	public Dict asDict() throws FileFormatException {
		return this;
	}
}
