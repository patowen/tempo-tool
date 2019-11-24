package net.patowen.tempotool.userinput;

import java.awt.Point;

public interface InputActionDrag extends InputAction {
	void onDrag(Point startRelative);
	
	void onCancel();
	
	void onEnd(Point startRelative);
	
	@Override
	default boolean cancelsDrag() {
		return true;
	}
}
