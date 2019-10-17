package net.patowen.songanalyzer.userinput;

import java.awt.Point;

public interface InputAction {
	boolean onAction(Point pos, double value);
	
	boolean cancelsDrag();
}
