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
		return spline.invEval(Math.floor(phase + 1), pos);
	}
}
