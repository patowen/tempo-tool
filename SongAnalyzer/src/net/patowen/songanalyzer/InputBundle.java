package net.patowen.songanalyzer;

import java.awt.Point;

public class InputBundle {
	public final InputHandler inputHandler;
	public final Point mousePos;
	
	public InputBundle(InputHandler inputHandler, Point mousePos) {
		this.inputHandler = inputHandler;
		this.mousePos = mousePos;
	}
}
