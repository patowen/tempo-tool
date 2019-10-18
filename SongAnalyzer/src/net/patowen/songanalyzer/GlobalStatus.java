package net.patowen.songanalyzer;

import java.awt.Component;
import java.io.File;

import net.patowen.songanalyzer.undo.UserActionList;

@SuppressWarnings("unused")
public class GlobalStatus {
	// All-encompassing audio file
	private AudioStream audioStream;
	
	// Saving
	private File defaultFolder;
	private File currentFile;
	
	// Refreshing the view
	private Component component;
	
	public GlobalStatus(Component component) {
		this.component = component;
	}
	
	public double getPlayPos() {
		if (audioStream == null) {
			return 0;
		}
		return audioStream.getPos();
	}
	
	// TODO: Remove?
	public void repaint() {
		component.repaint();
	}
}
