package net.patowen.songanalyzer;

import java.awt.Point;

import net.patowen.songanalyzer.Deck.MackSlot;

public class DeckInput {
	private final Deck deck;
	private final GlobalStatus globalStatus;
	
	public final InputHandler.Dragging resize;
	
	public DeckInput(Deck deck, GlobalStatus globalStatus) {
		this.deck = deck;
		this.globalStatus = globalStatus;
		this.resize = prepareResize();
	}
	
	public InputHandler.Dragging prepareResize() {
		InputHandler.Dragging resize = new InputHandler.Dragging();
		resize.cancelsDrag = true;
		resize.inputAction = new ActionResize(deck, globalStatus);
		return resize;
	}
	
	private static final class ActionResize implements InputActionDrag {
		private final Deck deck;
		private final GlobalStatus globalStatus;
		
		private Deck.MackSlot mackSlot;
		private int initialHeight;
		
		public ActionResize(Deck deck, GlobalStatus globalStatus) {
			this.deck = deck;
			this.globalStatus = globalStatus;
		}
		
		@Override
		public void onStart(Point nodeRelative) {
			this.mackSlot = ((MouseRegionMackBoundary)deck.getMouseRegion(nodeRelative.x, nodeRelative.y)).mackSlot;
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
		
	}
	
	static class MouseRegionMack implements MouseRegion {
		MackSlot mackSlot;
		Point mousePos;
		
		public MouseRegionMack(MackSlot mackSlot, Point mousePos) {
			this.mackSlot = mackSlot;
			this.mousePos = mousePos;
		}
	}
	
	static class MouseRegionMackBoundary implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionMackBoundary(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
	
	static class MouseRegionTab implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionTab(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
}
