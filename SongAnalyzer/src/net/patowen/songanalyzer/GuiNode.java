package net.patowen.songanalyzer;

import java.awt.Graphics2D;

public interface GuiNode {
	public void setPos(int x, int y);
	
	public void setSize(int width, int height);
	
	public void render(Graphics2D g);
	
	public GuiNode getMouseNode(int mouseX, int mouseY);
}
