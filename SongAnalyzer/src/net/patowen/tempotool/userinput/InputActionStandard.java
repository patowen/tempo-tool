package net.patowen.tempotool.userinput;

public interface InputActionStandard extends InputAction {
	@Override
	default boolean cancelsDrag() {
		return false;
	}
}
