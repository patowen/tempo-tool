package net.patowen.songanalyzer;

import java.awt.Point;

public interface InputActionDrag {
	void onDrag(Point startRelative);
	
	void onCancel();
	
	void onEnd(Point startRelative);
}
