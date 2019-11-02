package net.patowen.songanalyzer.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

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
	
	private final DeckBundle bundle;
	
	public SeekMack(DeckBundle bundle) {
		this.bundle = bundle;
		
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
		if (bundle.audioPlayer.hasAudioStream()) {
			g.setColor(new Color(128, 128, 128));
			int xLeft = bundle.trackBounds.secondsToPixel(0);
			int xRight = bundle.trackBounds.secondsToPixel(bundle.audioPlayer.getLength());
			g.fillRect(xLeft, 8, xRight - xLeft + 1, height - 16);
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
	
	private class Seek implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				bundle.audioPlayer.setPos(bundle.trackBounds.pixelToSeconds(pos.x));
				return true;
			}
			return false;
		}
	}
}