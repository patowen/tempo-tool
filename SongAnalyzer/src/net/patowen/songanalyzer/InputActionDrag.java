package net.patowen.songanalyzer;

import java.awt.Point;

public interface InputActionDrag {
	void onStart(Point nodeRelative);
	
	void onDrag(Point startRelative);
	
	void onCancel(Point startRelative);
	
	void onEnd(Point startRelative);
}
