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
