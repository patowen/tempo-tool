package net.patowen.tempotool.bundle;

import java.awt.Component;

import net.patowen.tempotool.AnimationController;
import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.Config;
import net.patowen.tempotool.DialogManager;
import net.patowen.tempotool.Ticker;
import net.patowen.tempotool.undo.UserActionList;

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
