package net.patowen.songanalyzer.old;

import net.patowen.songanalyzer.undo.UserAction;

public class UserActionRemoveMark implements UserAction {
	private MarkerLayer layer;
	private double mark;
	
	public UserActionRemoveMark(MarkerLayer layer, double mark) {
		this.layer = layer;
		this.mark = mark;
	}
	
	public void exec() {
		layer.removeMark(mark);
	}
	
	public void undo() {
		layer.addMark(mark);
	}
}
