package net.patowen.songanalyzer;

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
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
}
