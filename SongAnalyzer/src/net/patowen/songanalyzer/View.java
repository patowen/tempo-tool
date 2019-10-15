package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

public interface View {
	void render(Graphics2D g);
	
	InputAction applyInputAction(InputType inputType, Point mousePos, double value);
	
	MouseHoverFeedback applyMouseHover(Point mousePos);
}
