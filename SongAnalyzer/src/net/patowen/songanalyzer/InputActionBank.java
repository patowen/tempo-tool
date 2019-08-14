package net.patowen.songanalyzer;

import java.awt.Point;

public interface InputActionBank {
	void onStart(Point nodeRelative, double value);
	
	default void onDrag(Point startRelative) { // Cannot be null
		
	}
	
	default void onCancel(Point startRelative) { // Cannot be null
		
	}
	
	default void onEnd(Point startRelative) { // Cannot be null
		
	}
}
