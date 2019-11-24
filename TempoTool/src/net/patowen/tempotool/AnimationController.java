package net.patowen.tempotool;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

public class AnimationController {
	private final Component component;
	
	private Set<AnimationSource> runningAnimations;
	
	public static final int timerDelayMilliseconds = 30;
	private Timer timer;
	
	public AnimationController(Component component) {
		this.component = component;
		this.runningAnimations = new HashSet<>();
		
		timer = new Timer(timerDelayMilliseconds, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AnimationController.this.component.repaint();
			}
		});
	}
	
	public AnimationSource createAnimationSource() {
		return new AnimationSource(this);
	}
	
	public void enableAnimationSource(AnimationSource animationSource) {
		runningAnimations.add(animationSource);
		updateTimer();
	}
	
	public void disableAnimationSource(AnimationSource animationSource) {
		runningAnimations.remove(animationSource);
		updateTimer();
	}
	
	private void updateTimer() {
		if (!runningAnimations.isEmpty()) {
			timer.restart();
		} else {
			timer.stop();
		}
	}
	
	public void destroy() {
		runningAnimations.clear();
		timer.stop();
	}
}
