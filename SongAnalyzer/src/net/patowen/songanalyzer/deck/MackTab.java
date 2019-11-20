package net.patowen.songanalyzer.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.View;

public class MackTab extends View {
	private final MackRefs mackRefs;
	
	private final Mack mack;
	private final InputDictionary inputDictionary;
	
	public MackTab(DeckBundle bundle, Mack mack) {
		mackRefs = bundle.mackRefs;
		this.mack = mack;
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new SelectMack(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public void render(Graphics2D g) {
		if (mackRefs.selectedMack == mack) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
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
	
	private class SelectMack implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos) && mackRefs.selectedMack != mack) {
				mackRefs.selectedMack = mack;
				return true;
			}
			
			return false;
		}
	}
}
