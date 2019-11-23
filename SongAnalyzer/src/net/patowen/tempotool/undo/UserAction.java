package net.patowen.tempotool.undo;

public interface UserAction {
	public void exec();
	
	public void undo();
}
