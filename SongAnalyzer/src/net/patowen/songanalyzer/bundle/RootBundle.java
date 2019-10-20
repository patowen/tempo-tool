package net.patowen.songanalyzer.bundle;

import java.awt.Component;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.undo.UserActionList;

// Stores data relevant to the whole application
public class RootBundle {
	private Config config;
	private UserActionList userActionList;
	private DialogManager fileDialogManager;
	private AudioPlayer audioPlayer;
	
	public RootBundle(Component component) {
		config = new Config();
		userActionList = new UserActionList();
		fileDialogManager = new DialogManager(component);
		audioPlayer = new AudioPlayer(component);
	}
	
	public Config getConfig() {
		return config;
	}
	
	public UserActionList getUserActionList() {
		return userActionList;
	}
	
	public DialogManager getFileDialogManager() {
		return fileDialogManager;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}
}
