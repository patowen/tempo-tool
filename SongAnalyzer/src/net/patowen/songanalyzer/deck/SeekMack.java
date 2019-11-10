package net.patowen.songanalyzer.deck;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

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
