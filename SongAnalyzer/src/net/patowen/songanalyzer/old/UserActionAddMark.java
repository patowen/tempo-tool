package net.patowen.songanalyzer.old;

import net.patowen.songanalyzer.undo.UserAction;

public class UserActionAddMark implements UserAction {
	private MarkerLayer layer;
	private double mark;
	
	public UserActionAddMark(MarkerLayer layer, double mark) {
		this.layer = layer;
		this.mark = mark;
	}
	
	@Override
	public void exec() {
		layer.addMark(mark);
	}
	
	@Override
	public void undo() {
		layer.removeMark(mark);
	}
}
