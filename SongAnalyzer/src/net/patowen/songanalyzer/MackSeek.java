package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeScroll;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class MackSeek extends Mack {
	private InputDictionary inputDictionary;
	
	private TrackBounds trackBounds;
	
	public MackSeek(TrackBounds trackBounds) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		inputDictionary.constructDictionary();
		
		this.trackBounds = trackBounds;
	}
	
	@Override
	public int getType() {
		return 0;
	}
	
	@Override
	public int getMinimumHeight() {
		return 32;
	}
	
	@Override
	public int getDefaultHeight() {
		return 64;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public void render(Graphics2D g) {
		
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
				trackBounds.zoom(trackBounds.pixelToSeconds(pos.x), zoomFactor);
				return true;
			}
			return false;
		}
	}
}
