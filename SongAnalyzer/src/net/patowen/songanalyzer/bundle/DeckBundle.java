package net.patowen.songanalyzer.bundle;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.TrackBounds;
import net.patowen.songanalyzer.undo.UserActionList;

// Stores data relevant to a deck
public class DeckBundle {
	private RootBundle rootBundle;
	private TrackBounds trackBounds;
	
	public DeckBundle(RootBundle rootBundle) {
		this.rootBundle = rootBundle;
		trackBounds = new TrackBounds(0, 10);
	}
	
	public Config getConfig() {
		return rootBundle.getConfig();
	}
	
	public UserActionList getUserActionList() {
		return rootBundle.getUserActionList();
	}
	
	public DialogManager getFileDialogManager() {
		return rootBundle.getFileDialogManager();
	}
	
	public AudioPlayer getAudioPlayer() {
		return rootBundle.getAudioPlayer();
	}
	
	public TrackBounds getTrackBounds() {
		return trackBounds;
	}
}
