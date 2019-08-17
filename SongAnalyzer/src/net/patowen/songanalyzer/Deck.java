package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import net.patowen.songanalyzer.old.TrackBounds;

// The deck is main area of the application, a stack of macks (track layers) with a play bar.
public class Deck implements View {
	private GlobalStatus status;
	private ArrayList<MackSlot> mackSlots;
	private MackSlot activeMackSlot;
	private TrackBounds bounds;
	
	private int width, height;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	private int interBorderSelectionRange = 3;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(GlobalStatus status) {
		this.status = status;
		mackSlots = new ArrayList<>();
		
		for (int i=0; i<3; i++) {
			mackSlots.add(new MackSlot(new MackSeek()));
		}
	}
	
	private MouseRegion getMouseRegion(int mouseX, int mouseY) {
		int refY = outerBorderHeight;
		if (mouseY < refY) {
			return null;
		}
		for (int i = 0; i < mackSlots.size(); i++) {
			MackSlot mackSlot = mackSlots.get(i);
			if (mouseY - refY < mackSlot.height - interBorderSelectionRange) {
				if (mouseX >= outerBorderWidth && mouseX < outerBorderWidth + trackTabWidth) {
					return new MouseRegionTab(mackSlot);
				} else if (mouseX >= outerBorderWidth + trackTabWidth + trackTabBorderWidth && mouseX < width - outerBorderWidth) {
					return new MouseRegionMack(mackSlot, new Point(mouseX - (outerBorderWidth + trackTabWidth + trackTabBorderWidth), mouseY - refY));
				} else {
					return null;
				}
			} else if (mouseY - refY < mackSlot.height + interBorderHeight + interBorderSelectionRange) {
				return new MouseRegionMackBoundary(mackSlot);
			}
			refY += mackSlot.height + interBorderHeight;
		}
		return null;
	}
	
	private static interface MouseRegion {
		
	}
	
	private static class MouseRegionMack implements MouseRegion {
		MackSlot mackSlot;
		Point mousePos;
		
		public MouseRegionMack(MackSlot mackSlot, Point mousePos) {
			this.mackSlot = mackSlot;
			this.mousePos = mousePos;
		}
	}
	
	private static class MouseRegionMackBoundary implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionMackBoundary(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
	
	private static class MouseRegionTab implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionTab(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
	
	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width-1, height-1);
		
		AffineTransform savedTransform = g.getTransform();
		
		g.translate(outerBorderWidth, outerBorderHeight);
		int innerWidth = width - outerBorderWidth * 2;
		int trackWidth = innerWidth - trackTabWidth - trackTabBorderWidth;
//		bounds.setWidth(trackWidth);
		for (MackSlot mackSlot : mackSlots) {
			int layerHeight = mackSlot.height;
			AffineTransform savedTransform2 = g.getTransform();
			g.translate(trackTabWidth + trackTabBorderWidth, 0);
			Shape savedClip = g.getClip();
			g.clipRect(0, 0, trackWidth, layerHeight);
			mackSlot.mack.render(g); // trackWidth, layerHeight
			g.setClip(savedClip);
			g.setTransform(savedTransform2);
			g.setColor(Color.WHITE);
			g.setClip(null);
			g.drawLine(trackTabWidth, 0, trackTabWidth, layerHeight);
			g.drawLine(0, layerHeight, innerWidth, layerHeight);
			
			if (mackSlot == activeMackSlot) {
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, trackTabWidth, layerHeight);
			}
			
			g.translate(0, layerHeight + interBorderHeight);
		}
		
		g.setTransform(savedTransform);
		g.setColor(Color.GREEN);
//		int pos = bounds.secondsToPixel(status.getPlayPos()) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
//		g.drawLine(pos, 0, pos, height);
	}

	@Override
	public InputHandler getInputHandler(InputType inputType, Point mousePos) {
		if (inputType.isMouseBased()) {
			MouseRegion mouseRegion = getMouseRegion(mousePos.x, mousePos.y);
			if (mouseRegion instanceof MouseRegionMack) {
				MouseRegionMack mouseRegionMack = (MouseRegionMack) mouseRegion;
				mouseRegionMack.mackSlot.mack.getInputHandler(inputType, mouseRegionMack.mousePos);
			} else if (mouseRegion instanceof MouseRegionMackBoundary) {
				if (inputType.equals(new InputTypeMouse(MouseEvent.BUTTON1, false, false, false))) {
					InputHandler.Dragging dragging = new InputHandler.Dragging();
					dragging.cancelsDrag = true;
					dragging.inputType = inputType;
					dragging.mousePos = mousePos;
					dragging.inputAction = new ActionMackResize(((MouseRegionMackBoundary) mouseRegion).mackSlot);
					return dragging;
				}
			}
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	private void computeLayout() {
		int innerWidth = width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth;
		int y = outerBorderHeight;
		for (MackSlot mackSlot : mackSlots) {
			mackSlot.mack.setSize(innerWidth, mackSlot.height);
			y += mackSlot.height + interBorderHeight;
		}
	}
	
	private static class MackSlot {
		Mack mack;
		int height;
		
		public MackSlot(Mack mack) {
			this.mack = mack;
			this.height = mack.getDefaultHeight();
		}
	}
	
	private class ActionMackResize implements InputActionDrag {
		private MackSlot mackSlot;
		private int initialHeight;
		
		public ActionMackResize(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
			this.initialHeight = mackSlot.height;
		}
		
		@Override
		public void onStart(Point nodeRelative) {}
		
		@Override
		public void onDrag(Point startRelative) {
			mackSlot.height = initialHeight + startRelative.y;
			int minimumHeight = mackSlot.mack.getMinimumHeight();
			if (mackSlot.height < minimumHeight) {
				mackSlot.height = minimumHeight;
			}
			computeLayout();
			status.repaint();
		}
		
		@Override
		public void onCancel() {
			mackSlot.height = initialHeight;
			computeLayout();
			status.repaint();
		}
		
		@Override
		public void onEnd(Point startRelative) {}
	}
}
