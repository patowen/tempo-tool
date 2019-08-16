package net.patowen.songanalyzer;

public abstract class InputHandler {
	public GuiNode parentNode;
	public InputType inputType;
	public boolean cancelsDrag;
	
	public static final class Standard extends InputHandler {
		public InputActionStandard inputAction;
		public double factor;
	}
	
	public static final class Dragging extends InputHandler {
		public InputActionDrag inputAction;
	}
}
