package net.patowen.songanalyzer.deck.beatmack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.deck.Mack;
import net.patowen.songanalyzer.deck.TrackBounds;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionDrag;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class BeatMack extends Mack {
	public static final int type = 2;
	
	private InputDictionary inputDictionary;
	
	private final TrackBounds trackBounds;
	
	private Spline spline;
	
	private int selectionRange = 3;
	
	public BeatMack(DeckBundle bundle) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new MoveKnotAtMouse(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
		
		trackBounds = bundle.trackBounds;
		
		spline = new Spline(2);
		spline.x[0] = 1;
		spline.y[0] = 0;
		spline.x[1] = 2;
		spline.y[1] = 1;
		spline.x[2] = 5;
		spline.y[2] = 3;
		
		spline.computeSpline();
		
		bundle.ticker.addSource(new BeatMackTickerSource(spline));
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void render(Graphics2D g) {
		renderBeats(g);
		renderKnots(g);
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
	
	private void renderKnots(Graphics2D g) {
		g.setColor(Color.CYAN);
		for (int i=0; i<spline.x.length; i++) {
			int pixelX = trackBounds.secondsToPixel(spline.x[i]);
			g.drawLine(pixelX, height/2 - 4, pixelX, height/2 + 4);
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
		return inputDictionary.applyInput(inputType, mousePos, value);
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
	
	private class MoveKnotAtMouse implements InputActionDrag {
		private int knot;
		private double initialX;
		
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double seconds = trackBounds.pixelToSeconds(pos.x);
				int index = Arrays.binarySearch(spline.x, seconds);
				
				int lowerIndex = (index < 0) ? -index - 2 : index;
				int upperIndex = lowerIndex + 1;
				
				int closestIndex;
				if (lowerIndex < 0) {
					closestIndex = upperIndex;
				} else if (upperIndex >= spline.x.length) {
					closestIndex = lowerIndex;
				} else {
					if (seconds - spline.x[lowerIndex] < spline.x[upperIndex] - seconds) {
						closestIndex = lowerIndex;
					} else {
						closestIndex = upperIndex;
					}
				}
				
				int knotPixelX = trackBounds.secondsToPixel(spline.x[closestIndex]);
				if (pos.x >= knotPixelX - selectionRange && pos.x <= knotPixelX + selectionRange) {
					knot = closestIndex;
					initialX = spline.x[closestIndex];
					return true;
				}
			}
			return false;
		}

		@Override
		public void onDrag(Point startRelative) {
			spline.x[knot] = trackBounds.subpixelToSeconds(trackBounds.secondsToSubpixel(initialX) + startRelative.x);
			spline.computeSpline();
		}

		@Override
		public void onCancel() {
			spline.x[knot] = initialX;
			spline.computeSpline();
		}

		@Override
		public void onEnd(Point startRelative) {
		}
	}
}
