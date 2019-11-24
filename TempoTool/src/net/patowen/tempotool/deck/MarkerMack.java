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

package net.patowen.tempotool.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.Ticker;
import net.patowen.tempotool.TickerSource;
import net.patowen.tempotool.bundle.DeckBundle;
import net.patowen.tempotool.data.Arr;
import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.data.Obj;
import net.patowen.tempotool.undo.UserAction;
import net.patowen.tempotool.undo.UserActionList;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeKeyboard;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

public class MarkerMack extends Mack {
	public static final int type = 1;
	
	private InputDictionary inputDictionary;
	
	private final TrackBounds trackBounds;
	private final UserActionList userActionList;
	private final AudioPlayer audioPlayer;
	private final MackRefs mackRefs;
	private final Ticker ticker;
	
	private final TickerSource tickerSource;
	
	private TreeSet<Double> marks;
	
	private int markSelectionRange = 3;
	
	public MarkerMack(DeckBundle bundle) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new AddMarkAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new DeleteMarkAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new AddMarkAtPlayerPos(), new InputTypeKeyboard(KeyEvent.VK_N, false, false, false), 1));
		inputDictionary.constructDictionary();
		
		tickerSource = new TickerSource() {
			@Override
			public Double getNextTickInclusive(double pos) {
				return marks.ceiling(pos);
			}
			
			@Override
			public Double getNextTickExclusive(double pos) {
				return marks.higher(pos);
			}
		};
		
		this.trackBounds = bundle.trackBounds;
		this.userActionList = bundle.userActionList;
		this.audioPlayer = bundle.audioPlayer;
		this.mackRefs = bundle.mackRefs;
		this.ticker = bundle.ticker;
		
		this.marks = new TreeSet<>();
		ticker.addSource(tickerSource);
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(new Color(128, 128, 128));
		for (double mark : marks) {
			int x = trackBounds.secondsToPixel(mark);
			g.drawLine(x, 8, x, height - 9);
		}
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private Double getClickedMark(int mouseX) {
		double trackPos = trackBounds.pixelToSeconds(mouseX);
		Double closestMark = getClosestMark(trackPos);
		
		if (Math.abs(mouseX - trackBounds.secondsToPixel(closestMark)) <= markSelectionRange) {
			return closestMark;
		} else {
			return null;
		}
	}
	
	public Double getClosestMark(double trackPos) {
		Double lower = marks.floor(trackPos);
		Double upper = marks.ceiling(trackPos);
		
		if (lower == null && upper == null) {
			return null;
		}
		
		if (lower == null) {
			return upper;
		}
		
		if (upper == null) {
			return lower;
		}
		
		return upper - trackPos < trackPos - lower ? upper : lower;
	}
	
	private class AddMarkAtMouse implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double markPos = trackBounds.pixelToSeconds(pos.x);
				if (!marks.contains(markPos)) {
					userActionList.applyAction(new MarkCreationAction(Collections.singleton(markPos)));
					return true;
				}
			}
			return false;
		}
	}
	
	private class AddMarkAtPlayerPos implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (mackRefs.selectedMack == MarkerMack.this) {
				double markPos = audioPlayer.getPos();
				if (!marks.contains(markPos)) {
					userActionList.applyAction(new MarkCreationAction(Collections.singleton(markPos)));
					return true;
				}
			}
		
			return false;
		}
	}
	
	private class DeleteMarkAtMouse implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				Double clickedMark = getClickedMark(pos.x);
				if (clickedMark != null) {
					userActionList.applyAction(new MarkDeletionAction(Collections.singleton(clickedMark)));
					return true;
				}
			}
			return false;
		}
	}
	
	private class MarkCreationAction implements UserAction {
		private final Collection<Double> addedMarks;
		
		public MarkCreationAction(Collection<Double> addedMarks) {
			this.addedMarks = addedMarks;
		}
		
		@Override
		public void exec() {
			marks.addAll(addedMarks);
			audioPlayer.resetTicker();
		}

		@Override
		public void undo() {
			marks.removeAll(addedMarks);
			audioPlayer.resetTicker();
		}
	}
	
	private class MarkDeletionAction implements UserAction {
		private final Collection<Double> deletedMarks;
		
		public MarkDeletionAction(Collection<Double> deletedMarks) {
			this.deletedMarks = deletedMarks;
		}
		
		@Override
		public void exec() {
			marks.removeAll(deletedMarks);
			audioPlayer.resetTicker();
		}

		@Override
		public void undo() {
			marks.addAll(deletedMarks);
			audioPlayer.resetTicker();
		}
	}
	
	private interface Keys {
		int marks = 0;
	}
	
	@Override
	public void save(Dict dict) {
		Arr arr = new Arr();
		for (double mark : marks) {
			arr.add(mark);
		}
		dict.set(Keys.marks, arr);
	}

	@Override
	public void load(Dict dict) throws FileFormatException {
		Arr arr = dict.get(Keys.marks).asArr();
		for (Obj obj : arr.get()) {
			marks.add(obj.asDouble());
		}
	}
	
	@Override
	public void destroy() {
		ticker.removeSource(tickerSource);
	}
	
	@Override
	public void handleAudibleChange(boolean audible) {
		if (audible) {
			ticker.addSource(tickerSource);
		} else {
			ticker.removeSource(tickerSource);
		}
	}
}
