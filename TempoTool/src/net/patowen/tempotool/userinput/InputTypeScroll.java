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

package net.patowen.tempotool.userinput;

public final class InputTypeScroll implements InputType {
	private final boolean ctrl;
	private final boolean shift;
	private final boolean alt;
	
	public InputTypeScroll(boolean ctrl, boolean shift, boolean alt) {
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof InputTypeScroll)) {
			return false;
		}
		InputTypeScroll i = (InputTypeScroll)o;
		return ctrl == i.ctrl && shift == i.shift && alt == i.alt;
	}
	
	@Override
	public int hashCode() {
		return 0x300000 + (ctrl ? 4 : 0) + (shift ? 2 : 0) + (alt ? 1 : 0);
	}
	
	@Override
	public boolean fuzzyEquals(InputType inputType) {
		if (!(inputType instanceof InputTypeScroll)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isMouseBased() {
		return true;
	}
}
