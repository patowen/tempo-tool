package net.patowen.songanalyzer.deck.beatmack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

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
		spline.x[0] = 1;
		spline.y[0] = 0;
		spline.x[1] = 2;
		spline.y[1] = 0.5;
		spline.x[2] = 5;
		spline.y[2] = 3;
		
		spline.computeSpline();
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void render(Graphics2D g) {
		renderBeats(g);
	}
	
	private void renderBeats(Graphics2D g) {
		g.setColor(Color.GRAY);
		double currentPhase = spline.eval(trackBounds.subpixelToSeconds(0));
		for (int i=0; i<width-1; i++) {
			double nextPhase = spline.eval(trackBounds.subpixelToSeconds(i+1));
			if (Math.ceil(currentPhase) < Math.ceil(nextPhase)) {
				g.drawLine(i, 0, i, height-1);
			}
			currentPhase = nextPhase;
		}
	}
	
	@SuppressWarnings("unused")
	private void renderPhaseGraph(Graphics2D g) {
		g.setColor(Color.CYAN);
		double phasePrev = spline.eval(trackBounds.pixelToSeconds(-1));
		double phaseBase = Math.floor(phasePrev);
		for (int pixelX = 0; pixelX <= width; pixelX++) {
			double phase = spline.eval(trackBounds.pixelToSeconds(pixelX));
			while (true) {
				g.drawLine(pixelX - 1, phaseToPixel(phasePrev - phaseBase), pixelX, phaseToPixel(phase - phaseBase));
				if (phase - phaseBase > 1) {
					phaseBase += 1;
				} else {
					break;
				}
			}
			phasePrev = phase;
		}
	}
	
	private int phaseToPixel(double phase) {
		return height - 1 - (int)(Math.floor(phase * height));
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
