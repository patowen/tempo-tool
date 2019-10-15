package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import net.patowen.songanalyzer.old.TrackBounds;

// The deck is main area of the application, a stack of macks (track layers) with a play bar.
public class Deck implements View, DimWidthControlled, DimHeightControlled {
	private GlobalStatus status;
	private ArrayList<SuperMack> superMacks;
	private SuperMack activeSuperMack;
	private TrackBounds bounds;
	
	private int width, height;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(GlobalStatus status) {
		this.status = status;
		superMacks = new ArrayList<>();
		
		bounds = new TrackBounds(0, 10);
		
		for (int i=0; i<3; i++) {
			superMacks.add(new SuperMack(new MackSeek()));
		}
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
		
		for (SuperMack superMack : superMacks) {
			superMack.setWidth(width - outerBorderWidth * 2);
		}
	}
	
	@Override
	public void setHeight(int height) {
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
		for (SuperMack superMack : superMacks) {
			int layerHeight = superMack.getHeight();
			AffineTransform savedTransform2 = g.getTransform();
			g.translate(trackTabWidth + trackTabBorderWidth, 0);
			Shape savedClip = g.getClip();
			g.clipRect(0, 0, trackWidth, layerHeight);
			superMack.render(g); // trackWidth, layerHeight
			g.setClip(savedClip);
			g.setTransform(savedTransform2);
			g.setColor(Color.WHITE);
			g.setClip(null);
			g.drawLine(trackTabWidth, 0, trackTabWidth, layerHeight);
			g.drawLine(0, layerHeight, innerWidth, layerHeight);
			
			if (superMack == activeSuperMack) {
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
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		// TODO: Loop through all SuperMacks and apply the input action without checking first.
		// Proposal: Have views themselves determine if the cursor is in bounds. Don't leave
		// the responsibility to the parent view.
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
}
