package net.patowen.tempotool.userinput;

import java.awt.Point;

public interface InputAction {
	boolean onAction(Point pos, double value);
	
	boolean cancelsDrag();
}
