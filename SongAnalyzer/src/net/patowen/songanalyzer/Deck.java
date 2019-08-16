package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import net.patowen.songanalyzer.old.TrackBounds;
import net.patowen.songanalyzer.old.TrackLayer;

// The deck is main area of the application, a stack of track layers with a play bar.
public class Deck implements GuiNode {
	private GlobalStatus status;
	private ArrayList<TrackLayer> layers;
	private TrackLayer activeLayer;
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
		for (int i = 0; i < layers.size(); i++) {
			TrackLayer layer = layers.get(i);
			if (mouseY - refY < layer.getHeight() - interBorderSelectionRange) {
				if (mouseX >= outerBorderWidth && mouseX < outerBorderWidth + trackTabWidth) {
					return new MouseRegionTab(layer);
				} else if (mouseX >= outerBorderWidth + trackTabWidth + trackTabBorderWidth && mouseX < width - outerBorderWidth) {
					return new MouseRegionLayer(layer, mouseX - (outerBorderWidth + trackTabWidth + trackTabBorderWidth), mouseY - refY);
				} else {
					return null;
				}
			} else if (mouseY - refY < layer.getHeight() + interBorderHeight + interBorderSelectionRange) {
				return new MouseRegionLayerBoundary(layer);
			}
			refY += layer.getHeight() + interBorderHeight;
		}
		return null;
	}
	
	private interface MouseRegion {
		
	}
	
	private class MouseRegionLayer implements MouseRegion {
		TrackLayer layer;
		int mouseX;
		int mouseY;
		
		public MouseRegionLayer(TrackLayer layer, int mouseX, int mouseY) {
			this.layer = layer;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}
	}
	
	private class MouseRegionLayerBoundary implements MouseRegion {
		TrackLayer layer;
		
		public MouseRegionLayerBoundary(TrackLayer layer) {
			this.layer = layer;
		}
	}
	
	private class MouseRegionTab implements MouseRegion {
		TrackLayer layer;
		
		public MouseRegionTab(TrackLayer layer) {
			this.layer = layer;
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
		for (TrackLayer layer : layers) {
			int layerHeight = layer.getHeight();
			AffineTransform savedTransform2 = g.getTransform();
			g.translate(trackTabWidth + trackTabBorderWidth, 0);
			Shape savedClip = g.getClip();
			g.clipRect(0, 0, trackWidth, layerHeight);
			layer.render(g, trackWidth, layerHeight);
			g.setClip(savedClip);
			g.setTransform(savedTransform2);
			g.setColor(Color.WHITE);
			g.setClip(null);
			g.drawLine(trackTabWidth, 0, trackTabWidth, layerHeight);
			g.drawLine(0, layerHeight, innerWidth, layerHeight);
			
			if (layer == activeLayer) {
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
	public InputHandler getInputHandler(InputType inputType, Point mouse) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getPos() {
		// TODO Auto-generated method stub
		return null;
	}
}
