package net.patowen.songanalyzer.deck.beatmack;

import java.util.HashMap;
import java.util.Map;

import net.patowen.songanalyzer.TickerSource;

public class BeatMackTickerSource implements TickerSource {
	// TODO: Point to something that won't get replaced
	private Spline spline;
	
	// TODO: Invalidate cache when spline is adjusted
	
	// Note: The cache allows consistency to avoid any beats from being doubled
	// or missed when played back
	private Map<Integer, Double> beatToTimeCache = new HashMap<>();
	
	public BeatMackTickerSource(Spline spline) {
		this.spline = spline;
	}
	
	@Override
	public Double getNextTickInclusive(double pos) {
		return getNextTick(pos, true);
	}
	
	@Override
	public Double getNextTickExclusive(double pos) {
		return getNextTick(pos, false);
	}
	
	private Double getNextTick(double pos, boolean inclusive) {
		double phase = spline.eval(pos);
		int minNextBeat = (int)Math.floor(phase);
		Double minNextTick = beatToTimeCache.get(minNextBeat);
		if (minNextTick != null && (minNextTick > pos || (inclusive && minNextTick == pos))) {
			return minNextTick;
		}
		
		return null;
	}
	
	public Double getTimeForBeat(int beat, double guess) {
		Double cachedTime = beatToTimeCache.get(beat);
		if (cachedTime != null) {
			return cachedTime;
		}
		
		for (int i=0; i<10; i++) {
			double newGuess = guess - spline.eval(guess) / spline.derivative(guess);
			if (newGuess == guess) { // TODO: Consider allowing more error
				return guess;
			}
		}
		throw new RuntimeException("Newton's method failed to converge in time");
	}
}
