package net.patowen.tempotool.userinput;

public interface InputType {
	public boolean fuzzyEquals(InputType inputType);
	public boolean isMouseBased();
}
