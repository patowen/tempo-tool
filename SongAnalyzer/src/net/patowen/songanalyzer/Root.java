package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeKeyboard;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public class Root extends View implements DimWidthControlled, DimHeightControlled {
	private UserActionList userActionList;
	private Deck deck;
	private InputDictionary inputDictionary;
	
	public Root(UserActionList userActionList) {
		this.userActionList = userActionList;
		deck = new Deck(userActionList);
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Undo(), new InputTypeKeyboard(KeyEvent.VK_Z, true, false, false), 1));
		Redo redo = new Redo();
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Z, true, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Y, true, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	@Override
	public void render(Graphics2D g) {
		deck.forwardRender(g);
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction inputAction = inputDictionary.applyInput(inputType, mousePos, value);
		if (inputAction != null) {
			return inputAction;
		}
		
		return deck.forwardInput(inputType, mousePos, value);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return deck.forwardMouseHover(mousePos);
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		deck.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
		deck.setHeight(height);
	}
	
	private class Undo implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			userActionList.undo();
			return true;
		}
	}
	
	private class Redo implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			userActionList.redo();
			return true;
		}
	}
}
