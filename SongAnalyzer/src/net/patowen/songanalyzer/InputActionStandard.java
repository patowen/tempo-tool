package net.patowen.songanalyzer;

public interface InputActionStandard extends InputAction {
	default boolean cancelsDrag() {
		return false;
	}
}
