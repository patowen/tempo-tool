package net.patowen.songanalyzer;

public abstract class Mack extends View implements DimWidthControlled, DimHeightControlled {
	public abstract int getType();
	public abstract int getMinimumHeight();
	public abstract int getDefaultHeight();
}
