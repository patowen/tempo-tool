package net.patowen.songanalyzer.deck.beatmack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.deck.Mack;
import net.patowen.songanalyzer.deck.TrackBounds;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class BeatMack extends Mack {
	public static final int type = 2;
	
	private final TrackBounds trackBounds;
	
	private Spline spline;
	
	public BeatMack(DeckBundle bundle) {
		trackBounds = bundle.trackBounds;
		
		spline = new Spline(2);
		spline.x[0] = -1;
		spline.y[0] = 0.5;
		spline.x[1] = 0;
		spline.y[1] = 0;
		spline.x[2] = 3;
		spline.y[2] = 3;
		
		spline.computeSpline();
	}
	
	private double getPixelY(double realY) {
		return (double)height * (1.0 - (realY + 1.0) / 5.0);
	}
	
	private void drawCircle(Graphics2D g, double x, double y, double r) {
		g.fill(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.GREEN);
		for (int i=0; i<spline.x.length; i++) {
			double x = trackBounds.secondsToSubpixel(spline.x[i]);
			double y = getPixelY(spline.y[i]);
			drawCircle(g, x, y, 5);
		}
		
		g.setColor(Color.CYAN);
		for (int pixelX = 0; pixelX < width; pixelX ++) {
			double realX = trackBounds.pixelToSeconds(pixelX);
			double realY = spline.eval(realX);
			int pixelY = (int)Math.floor(0.5 + getPixelY(realY));
			
			g.drawLine(pixelX, pixelY, pixelX, pixelY);
		}
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}

	@Override
	public void save(Dict dict) {
	}

	@Override
	public void load(Dict dict) throws FileFormatException {
	}
}
