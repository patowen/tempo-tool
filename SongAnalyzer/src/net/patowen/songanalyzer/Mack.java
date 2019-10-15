package net.patowen.songanalyzer;

public interface Mack extends View, DimWidthControlled, DimHeightControlled {
	public int getType();
	public abstract int getMinimumHeight();
	public abstract int getDefaultHeight();
}
