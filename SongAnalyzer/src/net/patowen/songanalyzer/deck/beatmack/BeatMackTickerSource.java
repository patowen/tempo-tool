package net.patowen.songanalyzer.deck.beatmack;

import net.patowen.songanalyzer.TickerSource;

public class BeatMackTickerSource implements TickerSource {
	// TODO: Point to something that won't get replaced
	private Spline spline;
	
	public BeatMackTickerSource(Spline spline) {
		this.spline = spline;
	}
	
	@Override
	public Double getNextTickInclusive(double pos) {
		double phase = spline.eval(pos);
		if (phase == Math.floor(phase)) {
			return pos;
		}
		return getNextTickExclusive(pos);
	}
	
	@Override
	public Double getNextTickExclusive(double pos) {
		double phase = spline.eval(pos);
		return getTimeForPhase(Math.floor(phase + 1), pos);
	}
	
	public Double getTimeForPhase(double phase, double guess) {
		for (int i=0; i<10; i++) {
			double newGuess = guess - (spline.eval(guess) - phase) / spline.derivative(guess);
			if (newGuess == guess) { // TODO: Consider allowing more error
				return guess;
			}
			guess = newGuess;
		}
		throw new RuntimeException("Newton's method failed to converge in time");
	}
}
