package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import net.patowen.songanalyzer.old.TrackBounds;

// The deck is main area of the application, a stack of macks (track layers) with a play bar.
public class Deck implements View {
	private GlobalStatus status;
	private ArrayList<MackSlot> mackSlots;
	private MackSlot activeMackSlot;
	private TrackBounds bounds;
	
	private DeckInput deckInput;
	
	private int width, height;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	private int interBorderSelectionRange = 3;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(GlobalStatus status) {
		this.status = status;
		this.deckInput = new DeckInput(this, status);
		mackSlots = new ArrayList<>();
		
		bounds = new TrackBounds(0, 10);
		
		for (int i=0; i<3; i++) {
			mackSlots.add(new MackSlot(new MackSeek()));
		}
	}
	
	DeckInput.MouseRegion getMouseRegion(int mouseX, int mouseY) {
		int refY = outerBorderHeight;
		if (mouseY < refY) {
			return null;
		}
		for (int i = 0; i < mackSlots.size(); i++) {
			MackSlot mackSlot = mackSlots.get(i);
			if (mouseX >= outerBorderWidth && mouseX < outerBorderWidth + trackTabWidth) {
				if (mouseY - refY < mackSlot.height - interBorderSelectionRange) {
					return new DeckInput.MouseRegionTab(mackSlot);
				} else if (mouseY - refY < mackSlot.height + interBorderHeight + interBorderSelectionRange) {
					return new DeckInput.MouseRegionMackBoundary(deckInput, mackSlot);
				}
			} else if (mouseX >= outerBorderWidth + trackTabWidth + trackTabBorderWidth && mouseX < width - outerBorderWidth) {
				if (mouseY - refY < 0) {
					return null;
				} else if (mouseY - refY < mackSlot.height) {
					return new DeckInput.MouseRegionMack(mackSlot, new Point(mouseX - (outerBorderWidth + trackTabWidth + trackTabBorderWidth), mouseY - refY));
				}
			} else {
				return null;
			}
			refY += mackSlot.height + interBorderHeight;
		}
		return null;
	}
	
	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		computeLayout();
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
		
		int pos = bounds.secondsToPixel(status.getPlayPos()) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
		g.drawLine(pos, 0, pos, height);
	}

	@Override
	public InputHandler applyInputAction(InputType inputType, Point mousePos, double value) {
		if (inputType.isMouseBased()) {
			DeckInput.MouseRegion mouseRegion = getMouseRegion(mousePos.x, mousePos.y);
			if (mouseRegion != null) {
				return mouseRegion.handleInput(inputType, value);
			}
			return null;
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		if (mousePos == null) {
			return null;
		}
		
		DeckInput.MouseRegion mouseRegion = getMouseRegion(mousePos.x, mousePos.y);
		if (mouseRegion != null) {
			return mouseRegion.applyMouseHover();
		}
		return null;
	}
	
	public void computeLayout() {
		int innerWidth = width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth;
		for (MackSlot mackSlot : mackSlots) {
			mackSlot.mack.setSize(innerWidth, mackSlot.height);
		}
	}
	
	static class MackSlot {
		Mack mack;
		int height;
		
		public MackSlot(Mack mack) {
			this.mack = mack;
			this.height = mack.getDefaultHeight();
		}
	}
}
