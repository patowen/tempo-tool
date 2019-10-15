package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;

public class MackSeek implements Mack {
	private int width, height;
	
	private InputDictionary inputDictionary;
	
	public MackSeek() {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		inputDictionary.constructDictionary();
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
	public void setSize(int width, int height) {
		this.width = width;
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
	
	private static class Zoom implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
