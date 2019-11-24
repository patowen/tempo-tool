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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.patowen.tempotool.bundle.DeckBundle;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeKeyboard;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.MouseHoverFeedback;
import net.patowen.tempotool.view.View;

public class MackTab extends View {
	private final MackRefs mackRefs;
	
	private final Mack mack;
	private final InputDictionary inputDictionary;
	
	public MackTab(DeckBundle bundle, Mack mack) {
		mackRefs = bundle.mackRefs;
		this.mack = mack;
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new SelectMack(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new ToggleSound(), new InputTypeKeyboard(KeyEvent.VK_T, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public void render(Graphics2D g) {
		if (mackRefs.selectedMack == mack) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
		}
		
		if (mack.isAudible()) {
			g.setColor(Color.GREEN);
			g.fillRect(2, 2, width - 4, width - 4);
		}
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private class SelectMack implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos) && mackRefs.selectedMack != mack) {
				mackRefs.selectedMack = mack;
				return true;
			}
			
			return false;
		}
	}
	
	private class ToggleSound implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (mackRefs.selectedMack == mack) {
				mack.setAudible(!mack.isAudible());
				return true;
			}
			
			return false;
		}
	}
}
