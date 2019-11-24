package net.patowen.tempotool.bundle;

import java.awt.Component;

import javax.swing.JFrame;

import net.patowen.tempotool.AnimationController;
import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.Config;
import net.patowen.tempotool.DialogManager;
import net.patowen.tempotool.Ticker;
import net.patowen.tempotool.WindowManager;
import net.patowen.tempotool.undo.UserActionList;

// Stores data relevant to the whole application
public class RootBundle {
	public final Config config;
	public final UserActionList userActionList;
	public final DialogManager dialogManager;
	public final WindowManager windowManager;
	public final AnimationController animationController;
	public final Ticker ticker;
	public final AudioPlayer audioPlayer;
	
	public RootBundle(JFrame frame, Component component) {
		config = new Config();
		userActionList = new UserActionList();
		dialogManager = new DialogManager(component);
		windowManager = new WindowManager(frame);
		animationController = new AnimationController(component);
		ticker = new Ticker();
		audioPlayer = new AudioPlayer(animationController, ticker);
	}
}
