package net.patowen.songanalyzer;

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
