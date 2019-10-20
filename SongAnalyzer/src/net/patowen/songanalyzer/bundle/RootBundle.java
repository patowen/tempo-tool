package net.patowen.songanalyzer.bundle;

import java.awt.Component;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.undo.UserActionList;

// Stores data relevant to the whole application
public class RootBundle {
	private final Config config;
	private final UserActionList userActionList;
	private final DialogManager fileDialogManager;
	private final AudioPlayer audioPlayer;
	
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
	
	public DialogManager getDialogManager() {
		return fileDialogManager;
	}
	
	public AudioPlayer getAudioPlayer() {
		return audioPlayer;
	}
}
