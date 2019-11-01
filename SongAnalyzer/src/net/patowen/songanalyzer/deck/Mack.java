package net.patowen.songanalyzer.deck;

import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public abstract class Mack extends View implements DimWidthControlled, DimHeightControlled {
	public abstract int getType();
	
	public int getMinimumHeight() {
		return 32;
	}
	
	public int getDefaultHeight() {
		return 64;
	}
	
	public abstract void save(Dict dict);
	
	public abstract void load(Dict dict) throws FileFormatException;
}
