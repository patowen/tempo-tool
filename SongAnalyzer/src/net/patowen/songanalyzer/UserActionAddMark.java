package net.patowen.songanalyzer;

public class UserActionAddMark implements UserAction {
	private MarkerLayer layer;
	private double mark;
	
	public UserActionAddMark(MarkerLayer layer, double mark) {
		this.layer = layer;
		this.mark = mark;
	}
	
	public void exec() {
		layer.addMark(mark);
	}
	
	public void undo() {
		layer.removeMark(mark);
	}
}
