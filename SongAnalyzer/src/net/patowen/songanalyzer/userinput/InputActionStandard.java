package net.patowen.songanalyzer.userinput;

public interface InputActionStandard extends InputAction {
	@Override
	default boolean cancelsDrag() {
		return false;
	}
}
