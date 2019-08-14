package net.patowen.songanalyzer;

import java.io.File;

import net.patowen.songanalyzer.old.AudioStream;
import net.patowen.songanalyzer.old.UserActionList;

public class GlobalStatus {
	// All-encompassing audio file
	private AudioStream audioStream;
	
	// Actions and undoing
	private UserActionList userActionList;
	
	// Saving
	private File defaultFolder;
	private File currentFile;
	
	public double getPlayPos() {
		return audioStream.getPos();
	}
}
