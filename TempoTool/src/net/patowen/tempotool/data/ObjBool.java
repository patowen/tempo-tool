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
