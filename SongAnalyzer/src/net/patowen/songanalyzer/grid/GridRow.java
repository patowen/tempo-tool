package net.patowen.songanalyzer.grid;

public class GridRow {
	private int yPos;
	private int height;
	private boolean resizable;
	
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getYPos() {
		return yPos;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	
	public boolean isResizable() {
		return resizable;
	}
}
