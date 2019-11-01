package net.patowen.songanalyzer.view;

public class BasicSizer implements Sizer {
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public int getXPos() {
		return xPos;
	}
	
	@Override
	public int getYPos() {
		return yPos;
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
