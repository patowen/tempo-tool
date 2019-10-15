package net.patowen.songanalyzer;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class DeckInput {
	private final Deck deck;
	private final GlobalStatus globalStatus;
	
	public DeckInput(Deck deck, GlobalStatus globalStatus) {
		this.deck = deck;
		this.globalStatus = globalStatus;
	}
	
	static interface MouseRegion {
		public InputMapping handleInput(InputType inputType, double value);
		public MouseHoverFeedback applyMouseHover();
	}
	
	static class MouseRegionMack implements MouseRegion {
		SuperMack superMack;
		Point mousePos;
		
		public MouseRegionMack(SuperMack superMack, Point mousePos) {
			this.superMack = superMack;
			this.mousePos = mousePos;
		}
		
		public InputMapping handleInput(InputType inputType, double value) {
			return superMack.mack.applyInputAction(inputType, mousePos, value);
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return superMack.mack.applyMouseHover(mousePos);
		}
	}
	
	static class MouseRegionMackBoundary implements MouseRegion {
		DeckInput deckInput;
		SuperMack superMack;
		
		public MouseRegionMackBoundary(DeckInput deckInput, SuperMack superMack) {
			this.deckInput = deckInput;
			this.superMack = superMack;
		}
		
		public InputMapping handleInput(InputType inputType, double value) {
			if (inputType.fuzzyEquals(new InputTypeMouse(MouseEvent.BUTTON1, false, false, false))) {
				return deckInput.prepareResize(superMack);
			}
			return null;
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return new MouseHoverFeedback(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
	}
	
	static class MouseRegionTab implements MouseRegion {
		SuperMack superMack;
		
		public MouseRegionTab(SuperMack superMack) {
			this.superMack = superMack;
		}
		
		public InputMapping handleInput(InputType inputType, double value) {
			return null;
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return null;
		}
	}
}
