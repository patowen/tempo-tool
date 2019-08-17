package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

public interface View {
	public void setPos(int x, int y);
	
	public void setSize(int width, int height);
	
	public void render(Graphics2D g);
	
	public InputHandler getInputHandler(InputType inputType, Point mousePos);
}
