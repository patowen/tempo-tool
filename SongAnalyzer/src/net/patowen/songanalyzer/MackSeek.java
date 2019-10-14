package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;

public class MackSeek implements Mack {
	private int width, height;
	
	private HashMap<InputType, InputHandler> inputs;
	
	public MackSeek() {
		inputs.put(new InputTypeScroll(false, false, false), new InputHandler.Standard());
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
	public InputHandler applyInputAction(InputType inputType, Point mousePos, double value) {
		return null;
	}
	
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private static class Zoom implements InputActionStandard {
		// TODO: Inputs need to be more manipulatable than this.
	}
}
