package net.patowen.tempotool.userinput;

import java.awt.Point;

public class InputMapping {
	private InputAction inputAction;
	private InputType inputType;
	private double factor;
	
	public InputMapping(InputAction inputAction, InputType inputType, double factor) {
		this.inputAction = inputAction;
		this.inputType = inputType;
		this.factor = factor;
	}
	
	public boolean applyAction(Point pos, double value) {
		return inputAction.onAction(pos, value * factor);
	}
	
	public InputAction getInputAction() {
		return this.inputAction;
	}
	
	public InputType getInputType() {
		return inputType;
	}
}
