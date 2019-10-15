package net.patowen.songanalyzer;

import java.awt.Point;

public interface InputActionDrag extends InputAction {
	void onDrag(Point startRelative);
	
	void onCancel();
	
	void onEnd(Point startRelative);
	
	default boolean cancelsDrag() {
		return true;
	}
}
