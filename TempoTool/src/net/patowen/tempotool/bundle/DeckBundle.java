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
