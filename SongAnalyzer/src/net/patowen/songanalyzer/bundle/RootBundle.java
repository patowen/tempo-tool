package net.patowen.songanalyzer.bundle;

import java.awt.Component;

import net.patowen.songanalyzer.AnimationController;
import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.Config;
import net.patowen.songanalyzer.DialogManager;
import net.patowen.songanalyzer.Ticker;
import net.patowen.songanalyzer.undo.UserActionList;

// Stores data relevant to the whole application
public class RootBundle {
	public final Config config;
	public final UserActionList userActionList;
	public final DialogManager dialogManager;
	public final AnimationController animationController;
	public final Ticker ticker;
	public final AudioPlayer audioPlayer;
	
	public RootBundle(Component component) {
		config = new Config();
		userActionList = new UserActionList();
		dialogManager = new DialogManager(component);
		animationController = new AnimationController(component);
		ticker = new Ticker();
		audioPlayer = new AudioPlayer(animationController, ticker);
	}
}
