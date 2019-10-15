package net.patowen.songanalyzer;

import java.awt.Point;

public class InputBundle {
	public final InputMapping inputHandler;
	public final Point mousePos;
	
	public InputBundle(InputMapping inputHandler, Point mousePos) {
		this.inputHandler = inputHandler;
		this.mousePos = mousePos;
	}
}
