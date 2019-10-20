package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeScroll;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class MackSeek extends Mack {
	public static final int type = 0;
	
	private InputDictionary inputDictionary;
	
	private DeckBundle bundle;
	
	public MackSeek(DeckBundle bundle) {
		this.bundle = bundle;
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public void render(Graphics2D g) {
		if (bundle.getAudioPlayer().hasAudioStream()) {
			Shape prevClip = g.getClip();
			g.clipRect(0, 0, width, height);
			
			g.setColor(new Color(128, 128, 128));
			int xLeft = bundle.getTrackBounds().secondsToPixel(0);
			int xRight = bundle.getTrackBounds().secondsToPixel(bundle.getAudioPlayer().getLength());
			g.fillRect(xLeft, 8, xRight - xLeft + 1, height - 16);
			
			g.setClip(prevClip);
		}
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private class Zoom implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double zoomFactor = Math.exp(value * 0.1);
				bundle.getTrackBounds().zoom(bundle.getTrackBounds().pixelToSeconds(pos.x), zoomFactor);
				return true;
			}
			return false;
		}
	}

	@Override
	public void save(Dict dict) {
	}

	@Override
	public void load(Dict dict) throws FileFormatException {
	}
}
