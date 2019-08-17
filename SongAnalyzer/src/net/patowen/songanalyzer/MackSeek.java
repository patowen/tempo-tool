package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

public class MackSeek implements Mack {
	private int width, height;
	
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
	public InputHandler getInputHandler(InputType inputType, Point mousePos) {
		return null;
	}
}
