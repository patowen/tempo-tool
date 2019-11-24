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
import java.util.ArrayList;
import java.util.List;

public class Arr extends Obj {
	public static final byte type = 1;
	
	private List<Obj> arr;
	
	public Arr() {
		arr = new ArrayList<>();
	}
	
	public List<Obj> get() {
		return arr;
	}
	
	public void add(Obj value) {
		arr.add(value);
	}
	
	public void add(String value) {
		arr.add(new ObjString(value));
	}
	
	public void add(int value) {
		arr.add(new ObjInt(value));
	}
	
	public void add(double value) {
		arr.add(new ObjDouble(value));
	}
	
	public void add(boolean value) {
		arr.add(new ObjBool(value));
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
	
	@Override
	public Arr asArr() throws FileFormatException {
		return this;
	}
}
