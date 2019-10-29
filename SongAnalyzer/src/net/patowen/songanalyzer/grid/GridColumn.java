package net.patowen.songanalyzer.grid;

public class GridColumn {
	private int xPos = 0;
	private int width = 0;
	private boolean resizable = false;
	
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getXPos() {
		return xPos;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	
	public boolean isResizable() {
		return resizable;
	}
}
