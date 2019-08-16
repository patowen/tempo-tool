package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import net.patowen.songanalyzer.old.TrackBounds;

// The deck is main area of the application, a stack of track layers with a play bar.
public class Deck implements GuiNode {
	private GlobalStatus status;
	private ArrayList<MackSlot> mackSlots;
	private MackSlot activeMackSlot;
	private TrackBounds bounds;
	
	private int width, height;
	private int x, y;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	private int interBorderSelectionRange = 3;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
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
					return new MouseRegionMack(mackSlot.mack, mouseX - (outerBorderWidth + trackTabWidth + trackTabBorderWidth), mouseY - refY);
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
	
	private interface MouseRegion {
		
	}
	
	private class MouseRegionMack implements MouseRegion {
		Mack mack;
		int mouseX;
		int mouseY;
		
		public MouseRegionMack(Mack mack, int mouseX, int mouseY) {
			this.mack = mack;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}
	}
	
	private class MouseRegionMackBoundary implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionMackBoundary(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
	
	private class MouseRegionTab implements MouseRegion {
		MackSlot mackSlot;
		
		public MouseRegionTab(MackSlot mackSlot) {
			this.mackSlot = mackSlot;
		}
	}
	
	@Override
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
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
		bounds.setWidth(trackWidth);
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
	public InputHandler getInputHandler(InputType inputType, Point origin, Point mouse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getPos() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static class MackSlot {
		Mack mack;
		int height;
		
		public MackSlot(Mack mack) {
			this.mack = mack;
			this.height = mack.getDefaultHeight();
		}
	}
}
