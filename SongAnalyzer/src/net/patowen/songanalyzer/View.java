package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

public interface View {
	void setSize(int width, int height);
	
	void render(Graphics2D g);
	
	InputHandler applyInputAction(InputType inputType, Point mousePos, double value);
}
