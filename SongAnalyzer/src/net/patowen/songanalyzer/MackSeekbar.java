package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

public class MackSeekbar implements Mack {
	@Override
	public int getType() {
		return 0;
	}
	
	@Override
	public int getMinimumHeight() {
		// TODO Auto-generated method stub
		return 32;
	}
	
	@Override
	public int getDefaultHeight() {
		// TODO Auto-generated method stub
		return 16;
	}
	
	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public InputHandler getInputHandler(InputType inputType, Point mousePos) {
		// TODO Auto-generated method stub
		return null;
	}
}
