package net.patowen.songanalyzer.undo;

public interface UserAction {
	public void exec();
	
	public void undo();
}
