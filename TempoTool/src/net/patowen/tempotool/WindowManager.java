package net.patowen.tempotool;

import java.nio.file.Path;

import javax.swing.JFrame;

public class WindowManager {
	public enum DialogKind {
		SAVE,
		OPEN
	}
	
	private JFrame frame;
	
	public WindowManager(JFrame frame) {
		this.frame = frame;
	}
	
	public void setFile(Path path) {
		if (path == null) {
			frame.setTitle("Tempo Tool");
		} else {
			frame.setTitle(path.getFileName().toString() + " - Tempo Tool");
		}
	}
}
