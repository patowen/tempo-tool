package net.patowen.songanalyzer.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.nio.file.Path;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthFree;
import net.patowen.songanalyzer.view.View;

public class AudioFileSelector extends View implements DimWidthFree, DimHeightControlled {
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
	
	public int getPreferredWidth() {
		return 200;
	}
	
	@Override
	public void render(Graphics2D g) {
		Shape prevClip = g.getClip();
		g.clipRect(0, 0, width, height);
		
		g.setColor(Color.WHITE);
		Path audioFile = audioPlayer.getAudioFile();
		String text;
		if (audioFile == null) {
			text = "<no audio selected>";
		} else {
			text = audioFile.getFileName().toString();
		}
		g.drawString(text, 12, height - 12);
		
		g.setClip(prevClip);
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
	public int getWidth() {
		return width;
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
