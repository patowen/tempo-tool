package net.patowen.songanalyzer;

public final class InputTypeScroll implements InputType {
	private final boolean ctrl;
	private final boolean shift;
	private final boolean alt;
	
	public InputTypeScroll(boolean ctrl, boolean shift, boolean alt) {
		this.ctrl = ctrl;
		this.shift = shift;
		this.alt = alt;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof InputTypeScroll)) {
			return false;
		}
		InputTypeScroll i = (InputTypeScroll)o;
		return ctrl == i.ctrl && shift == i.shift && alt == i.alt;
	}
	
	@Override
	public int hashCode() {
		return 0x300000 + (ctrl ? 4 : 0) + (shift ? 2 : 0) + (alt ? 1 : 0);
	}
	
	@Override
	public boolean fuzzyEquals(InputType inputType) {
		if (!(inputType instanceof InputTypeScroll)) {
			return false;
		}
		return true;
	}
}
