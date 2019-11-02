package net.patowen.songanalyzer.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import net.patowen.songanalyzer.TickerSource;
import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Arr;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.data.Obj;
import net.patowen.songanalyzer.undo.UserAction;
import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class MackMarker extends Mack {
	public static final int type = 1;
	
	private InputDictionary inputDictionary;
	
	private final TrackBounds trackBounds;
	private final UserActionList userActionList;
	
	private TreeSet<Double> marks;
	
	private int markSelectionRange = 3;
	
	public MackMarker(DeckBundle bundle) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new AddMarkAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new DeleteMarkAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, true, false), 1));
		inputDictionary.constructDictionary();
		
		bundle.ticker.addSource(new TickerSource() {
			@Override
			public Double getNextTickInclusive(double pos) {
				return marks.ceiling(pos);
			}
			
			@Override
			public Double getNextTickExclusive(double pos) {
				return marks.higher(pos);
			}
		});
		
		this.trackBounds = bundle.trackBounds;
		this.userActionList = bundle.userActionList;
		this.marks = new TreeSet<>();
	}
	
	@Override
	public int getType() {
		return type;
	}
	
	@Override
	public void render(Graphics2D g) {
		Shape prevClip = g.getClip();
		g.clipRect(0, 0, width, height);
		
		g.setColor(new Color(128, 128, 128));
		for (double mark : marks) {
			int x = trackBounds.secondsToPixel(mark);
			g.drawLine(x, 8, x, height - 9);
		}
		
		g.setClip(prevClip);
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
	
	private Double getClosestMark(double trackPos) {
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
		}

		@Override
		public void undo() {
			marks.removeAll(addedMarks);
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
		}

		@Override
		public void undo() {
			marks.addAll(deletedMarks);
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
}
