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
