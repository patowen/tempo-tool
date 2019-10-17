package net.patowen.songanalyzer.userinput;

public interface InputActionStandard extends InputAction {
	default boolean cancelsDrag() {
		return false;
	}
}
