package net.patowen.songanalyzer;

public interface InputHandler {
	void onStart(int nodeRelativeX, int nodeRelativeY, double value);
	
	default void onDrag(int startRelativeX, int startRelativeY) {
		
	}
	
	default void onCancel(int startRelativeX, int startRelativeY) {
		
	}
	
	default void onEnd(int startRelativeX, int startRelativeY) {
		
	}
}
