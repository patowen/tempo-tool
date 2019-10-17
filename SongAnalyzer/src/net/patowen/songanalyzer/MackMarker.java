package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

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
	private InputDictionary inputDictionary;
	
	private TrackBounds trackBounds;
	private UserActionList userActionList;
	
	private TreeSet<Double> marks;
	
	public MackMarker(TrackBounds trackBounds, UserActionList userActionList) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new AddMark(), new InputTypeMouse(MouseEvent.BUTTON3, false, false, false), 1));
		inputDictionary.constructDictionary();
		
		this.trackBounds = trackBounds;
		this.userActionList = userActionList;
		this.marks = new TreeSet<>();
	}
	
	@Override
	public int getType() {
		return 1;
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
	
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private class AddMark implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				userActionList.applyAction(new UserAction() {
					@Override
					public void exec() {
						marks.add(trackBounds.pixelToSeconds(pos.x));
					}

					@Override
					public void undo() {
						marks.remove(trackBounds.pixelToSeconds(pos.x));
					}
				});
				
				return true;
			}
			return false;
		}
	}
}
