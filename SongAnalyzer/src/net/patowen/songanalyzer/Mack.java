package net.patowen.songanalyzer;

import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public abstract class Mack extends View implements DimWidthControlled, DimHeightControlled {
	public abstract int getType();
	public abstract int getMinimumHeight();
	public abstract int getDefaultHeight();
}
