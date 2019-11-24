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

package net.patowen.tempotool.deck;

import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.view.View;

public abstract class Mack extends View {
	private boolean audible = true;
	
	public abstract int getType();
	
	public int getMinimumHeight() {
		return 32;
	}
	
	public int getDefaultHeight() {
		return 64;
	}
	
	public abstract void save(Dict dict);
	
	public abstract void load(Dict dict) throws FileFormatException;
	
	public abstract void destroy();
	
	public final boolean isAudible() {
		return audible;
	}
	
	public final void setAudible(boolean audible) {
		if (audible != this.audible) {
			this.audible = audible;
			handleAudibleChange(audible);
		}
	}
	
	protected void handleAudibleChange(boolean audible) {
	}
}
