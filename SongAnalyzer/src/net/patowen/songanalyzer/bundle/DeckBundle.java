package net.patowen.songanalyzer.bundle;

import net.patowen.songanalyzer.AnimationController;
import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.TrackBounds;
import net.patowen.songanalyzer.undo.UserActionList;

// Stores data relevant to a deck
public class DeckBundle {
	// From RootBundle
	public final Config config;
	public final UserActionList userActionList;
	public final DialogManager fileDialogManager;
	public final AnimationController animationController;
	public final AudioPlayer audioPlayer;
	
	public final TrackBounds trackBounds;
	
	public DeckBundle(RootBundle rootBundle) {
		config = rootBundle.config;
		userActionList = rootBundle.userActionList;
		fileDialogManager = rootBundle.dialogManager;
		animationController = rootBundle.animationController;
		audioPlayer = rootBundle.audioPlayer;
		
		trackBounds = new TrackBounds(0, 60);
	}
}
