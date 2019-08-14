package net.patowen.songanalyzer.old;

import java.util.ArrayList;

public class Ticker {
	private ArrayList<TickerSource> sources;
	
	private Double lastPlayedTick;
	private Double nextTick;
	
	public Ticker() {
		sources = new ArrayList<>();
		lastPlayedTick = null;
		nextTick = null;
	}
	
	public void addSource(TickerSource source) {
		sources.add(source);
	}
	
	public void removeSource(TickerSource source) {
		sources.remove(source);
	}
	
	public void removeAllSources() {
		sources.clear();
	}
	
	public void reset(double startPos) {
		lastPlayedTick = null;
		nextTick = computeNextTickInclusive(startPos);
	}
	
	public Double getNextTick(double pos) {
		if (nextTick != null && pos > nextTick) {
			nextTick = computeNextTickInclusive(nextTick);
		} else if (lastPlayedTick != null && pos < lastPlayedTick) {
			nextTick = computeNextTickExclusive(lastPlayedTick);
		} else {
			nextTick = computeNextTickInclusive(pos);
		}
		return nextTick;
	}
	
	public Double getNextTick() {
		return nextTick;
	}
	
	public void markNextTickPlayed() {
		lastPlayedTick = nextTick;
		nextTick = computeNextTickExclusive(lastPlayedTick);
	}
	
	private Double computeNextTickInclusive(double pos) {
		Double tick = null;
		for (TickerSource source : sources) {
			Double candidate = source.getNextTickInclusive(pos);
			if (tick == null || (candidate != null && candidate < tick)) {
				tick = candidate;
			}
		}
		return tick;
	}
	
	private Double computeNextTickExclusive(double pos) {
		Double tick = null;
		for (TickerSource source : sources) {
			Double candidate = source.getNextTickExclusive(pos);
			if (tick == null || (candidate != null && candidate < tick)) {
				tick = candidate;
			}
		}
		return tick;
	}
}
