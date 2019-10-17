package net.patowen.songanalyzer.userinput;

import java.awt.Cursor;

public final class MouseHoverFeedback {
	private Cursor cursor;
	
	public MouseHoverFeedback(Cursor cursor) {
		this.cursor = cursor;
	}
	
	public Cursor getCursor() {
		return cursor;
	}
}
