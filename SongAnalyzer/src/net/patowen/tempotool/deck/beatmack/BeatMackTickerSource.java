package net.patowen.tempotool.deck.beatmack;

import net.patowen.tempotool.TickerSource;

public class BeatMackTickerSource implements TickerSource {
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
