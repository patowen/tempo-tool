package net.patowen.tempotool.bundle;

import net.patowen.tempotool.AnimationController;
import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.Config;
import net.patowen.tempotool.DialogManager;
import net.patowen.tempotool.Ticker;
import net.patowen.tempotool.deck.MackRefs;
import net.patowen.tempotool.deck.TrackBounds;
import net.patowen.tempotool.undo.UserActionList;

// Stores data relevant to a deck
public class DeckBundle {
	// From RootBundle
	public final Config config;
	public final UserActionList userActionList;
	public final DialogManager fileDialogManager;
	public final AnimationController animationController;
	public final Ticker ticker;
	public final AudioPlayer audioPlayer;
	
	public final TrackBounds trackBounds;
	public final MackRefs mackRefs;
	
	public DeckBundle(RootBundle rootBundle) {
		config = rootBundle.config;
		userActionList = rootBundle.userActionList;
		fileDialogManager = rootBundle.dialogManager;
		animationController = rootBundle.animationController;
		ticker = rootBundle.ticker;
		audioPlayer = rootBundle.audioPlayer;
		
		trackBounds = new TrackBounds(0, 60);
		mackRefs = new MackRefs();
	}
}
