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

import java.awt.Point;

public interface InputActionDrag extends InputAction {
	void onDrag(Point startRelative);
	
	void onCancel();
	
	void onEnd(Point startRelative);
	
	@Override
	default boolean cancelsDrag() {
		return true;
	}
}