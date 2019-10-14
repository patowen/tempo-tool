package net.patowen.songanalyzer;

import java.awt.Component;
import java.io.File;

import net.patowen.songanalyzer.old.UserActionList;

public class GlobalStatus {
	// All-encompassing audio file
	private AudioStream audioStream;
	
	// Actions and undoing
	private UserActionList userActionList;
	
	// Saving
	private File defaultFolder;
	private File currentFile;
	
	// Refreshing the view
	private Component component;
	
	public GlobalStatus(Component component) {
		this.component = component;
	}
	
	public double getPlayPos() {
		return audioStream.getPos();
	}
	
	public void repaint() {
		component.repaint();
	}
}
