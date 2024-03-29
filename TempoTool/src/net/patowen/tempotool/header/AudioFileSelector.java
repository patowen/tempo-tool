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

package net.patowen.tempotool.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.Config;
import net.patowen.tempotool.DialogManager;
import net.patowen.tempotool.bundle.RootBundle;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

public class AudioFileSelector extends HeaderView {
	private final Config config;
	private final DialogManager dialogManager;
	private final AudioPlayer audioPlayer;
	
	private InputDictionary inputDictionary;
	
	public AudioFileSelector(RootBundle bundle) {
		config = bundle.config;
		dialogManager = bundle.dialogManager;
		audioPlayer = bundle.audioPlayer;
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new OpenAudio(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public int getPreferredWidth() {
		return 200;
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		Path audioFile = audioPlayer.getAudioFile();
		String text;
		if (audioFile == null) {
			text = "<no audio selected>";
		} else {
			text = audioFile.getFileName().toString();
		}
		g.drawString(text, 12, height - 12);
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private class OpenAudio implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (pos.x >= 0 && pos.y >= 0 && pos.x < width && pos.y < height) {
				audioPlayer.chooseAudioFileFromUser(config, dialogManager);
				return true;
			}
			
			return false;
		}
	}
}
