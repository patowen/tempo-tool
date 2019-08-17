package net.patowen.songanalyzer;

public final class InputTypeKeyboard implements InputType {
	private final int button;
	private final boolean ctrl;
	private final boolean shift;
	private final boolean alt;
	
	public InputTypeKeyboard(int button, boolean ctrl, boolean shift, boolean alt) {
		this.button = button;
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof InputTypeKeyboard)) {
			return false;
		}
		InputTypeKeyboard i = (InputTypeKeyboard)o;
		return button == i.button && ctrl == i.ctrl && shift == i.shift && alt == i.alt;
	}
	
	@Override
	public int hashCode() {
		return 0x200000 + button * 8 + (ctrl ? 4 : 0) + (shift ? 2 : 0) + (alt ? 1 : 0);
	}
	
	@Override
	public boolean fuzzyEquals(InputType inputType) {
		if (!(inputType instanceof InputTypeKeyboard)) {
			return false;
		}
		return ((InputTypeKeyboard) inputType).button == button;
	}

	@Override
	public boolean isMouseBased() {
		return false;
	}
}
