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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.bundle.DeckBundle;
import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

public class SeekMack extends Mack {
	public static final int type = 0;
	
	private InputDictionary inputDictionary;
	
	private final AudioPlayer audioPlayer;
	private final TrackBounds trackBounds;
	
	public SeekMack(DeckBundle bundle) {
		this.audioPlayer = bundle.audioPlayer;
		this.trackBounds = bundle.trackBounds;
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Seek(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public void render(Graphics2D g) {
		if (audioPlayer.hasAudioStream()) {
			double start = trackBounds.pixelToSeconds(0);
			double end = trackBounds.pixelToSeconds(width);
			audioPlayer.visualize(g, width, height, start, end - start);
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

	@Override
	public void save(Dict dict) {
	}

	@Override
	public void load(Dict dict) throws FileFormatException {
	}
	
	@Override
	public void destroy() {
	}
	
	private class Seek implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				audioPlayer.setPos(trackBounds.pixelToSeconds(pos.x));
				return true;
			}
			return false;
		}
	}
}
