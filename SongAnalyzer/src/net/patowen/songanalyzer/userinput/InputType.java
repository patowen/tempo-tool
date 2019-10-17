package net.patowen.songanalyzer.userinput;

public interface InputType {
	public boolean fuzzyEquals(InputType inputType);
	public boolean isMouseBased();
}
