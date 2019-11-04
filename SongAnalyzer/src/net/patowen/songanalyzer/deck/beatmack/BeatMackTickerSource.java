package net.patowen.songanalyzer.deck.beatmack;

import net.patowen.songanalyzer.TickerSource;

public class BeatMackTickerSource implements TickerSource {
	// TODO: Point to something that won't get replaced
	private BeatFunction beatFunction;
	
	public BeatMackTickerSource(BeatFunction beatFunction) {
		this.beatFunction = beatFunction;
	}
	
	@Override
	public Double getNextTickInclusive(double pos) {
		double phase = beatFunction.getPhaseFromTime(pos);
		if (phase == Math.floor(phase)) {
			return pos;
		}
		return getNextTickExclusive(pos);
	}
	
	@Override
	public Double getNextTickExclusive(double pos) {
		return beatFunction.findTimeForNextBeat(pos);
	}
}
