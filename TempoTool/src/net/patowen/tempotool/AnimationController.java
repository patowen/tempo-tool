/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

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
