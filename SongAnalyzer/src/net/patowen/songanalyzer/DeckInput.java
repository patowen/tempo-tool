package net.patowen.songanalyzer;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.patowen.songanalyzer.Deck.MackSlot;

public class DeckInput {
	private final Deck deck;
	private final GlobalStatus globalStatus;
	
	//public final InputHandler.Dragging resize;
	//public final ActionResize actionResize;
	
	public DeckInput(Deck deck, GlobalStatus globalStatus) {
		this.deck = deck;
		this.globalStatus = globalStatus;
	}
	
	public InputHandler.Dragging prepareResize(MackSlot mackSlot) {
		InputHandler.Dragging resize = new InputHandler.Dragging();
		resize.cancelsDrag = true;
		resize.inputAction = new ActionResize(deck, globalStatus, mackSlot);
		return resize;
	}
	
	public static final class ActionResize implements InputActionDrag {
		private final Deck deck;
		private final GlobalStatus globalStatus;
		
		private Deck.MackSlot mackSlot;
		private int initialHeight;
		
		public ActionResize(Deck deck, GlobalStatus globalStatus, MackSlot mackSlot) {
			this.deck = deck;
			this.globalStatus = globalStatus;
			this.mackSlot = mackSlot;
			this.initialHeight = mackSlot.height;
		}
		
		@Override
		public void onDrag(Point startRelative) {
			mackSlot.height = initialHeight + startRelative.y;
			int minimumHeight = mackSlot.mack.getMinimumHeight();
			if (mackSlot.height < minimumHeight) {
				mackSlot.height = minimumHeight;
			}
			deck.computeLayout();
			globalStatus.repaint();
		}
		
		@Override
		public void onCancel() {
			mackSlot.height = initialHeight;
			deck.computeLayout();
			globalStatus.repaint();
		}
		
		@Override
		public void onEnd(Point startRelative) {}
	}
	
	static interface MouseRegion {
		public InputHandler handleInput(InputType inputType, double value);
		public MouseHoverFeedback applyMouseHover();
	}
	
	static class MouseRegionMack implements MouseRegion {
		MackSlot mackSlot;
		Point mousePos;
		
		public MouseRegionMack(MackSlot mackSlot, Point mousePos) {
			this.mackSlot = mackSlot;
			this.mousePos = mousePos;
		}
		
		public InputHandler handleInput(InputType inputType, double value) {
			return mackSlot.mack.applyInputAction(inputType, mousePos, value);
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return mackSlot.mack.applyMouseHover(mousePos);
		}
	}
	
	static class MouseRegionMackBoundary implements MouseRegion {
		DeckInput deckInput;
		MackSlot mackSlot;
		
		public MouseRegionMackBoundary(DeckInput deckInput, MackSlot mackSlot) {
			this.deckInput = deckInput;
			this.mackSlot = mackSlot;
		}
		
		public InputHandler handleInput(InputType inputType, double value) {
			if (inputType.fuzzyEquals(new InputTypeMouse(MouseEvent.BUTTON1, false, false, false))) {
				return deckInput.prepareResize(mackSlot);
			}
			return null;
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return new MouseHoverFeedback(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
	}
	
	static class MouseRegionTab implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionTab(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
		
		public InputHandler handleInput(InputType inputType, double value) {
			return null;
		}
		
		public MouseHoverFeedback applyMouseHover() {
			return null;
		}
	}
}
