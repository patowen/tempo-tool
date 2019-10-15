package net.patowen.songanalyzer;

import java.awt.Graphics2D;

public class SuperMack {
	private Mack mack;
	private int height;
	
	private int width;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public SuperMack(Mack mack) {
		this.mack = mack;
		this.height = mack.getDefaultHeight();
	}
	
	public void render(Graphics2D g) {
		mack.render(g);
	}
	
	public int getHeight() {
		return height;
	}
	
	public void trySetHeight(int height) {
		this.height = height;
		int minimumHeight = mack.getMinimumHeight();
		if (height < minimumHeight) {
			height = minimumHeight;
		}
		updateMackSize();
	}
	
	public void setWidth(int width) {
		this.width = width;
		updateMackSize();
	}
	
	private void updateMackSize() {
		mack.setSize(width - trackTabWidth - trackTabBorderWidth, height);
	}
}