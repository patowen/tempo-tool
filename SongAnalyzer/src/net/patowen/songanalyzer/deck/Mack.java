package net.patowen.songanalyzer.deck;

import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.view.View;

public abstract class Mack extends View {
	public abstract int getType();
	
	public int getMinimumHeight() {
		return 32;
	}
	
	public int getDefaultHeight() {
		return 64;
	}
	
	public abstract void save(Dict dict);
	
	public abstract void load(Dict dict) throws FileFormatException;
	
	public abstract void destroy();
}
