package net.patowen.songanalyzer;

import net.patowen.songanalyzer.view.Sizer;

public class RootSizer implements Sizer {
	private int width;
	private int height;
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getXPos() {
		return 0;
	}
	
	@Override
	public int getYPos() {
		return 0;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}
}
