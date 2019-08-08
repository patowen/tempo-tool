package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

// The deck is main area of the application, a stack of track layers with a play bar.
public class Deck {
	private GlobalStatus status;
	private ArrayList<TrackLayer> layers;
	private TrackLayer activeLayer;
	private TrackBounds bounds;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	private int interBorderSelectionRange = 3;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public void render(Graphics2D g, int width, int height) {
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
	
	private class MouseRegion {
		int cornerX;
		int cornerY;
		int layerIndex;
		boolean isLayer;
		boolean isLayerBoundary;
		boolean isTab;
		
		public MouseRegion(int cornerX, int cornerY) {
			this.cornerX = cornerX;
			this.cornerY = cornerY;
			isLayerBoundary = false;
			isLayer = false;
			isTab = false;
		}
	}
	
	private class MouseRegionLayer {
		TrackLayer layer;
		int mouseX;
		int mouseY;
		
		public MouseRegionLayer(TrackLayer layer, int mouseX, int mouseY) {
			this.layer = layer;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}
	}
}
