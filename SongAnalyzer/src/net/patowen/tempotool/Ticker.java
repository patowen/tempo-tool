package net.patowen.tempotool;

import java.util.ArrayList;

public class Ticker {
	private ArrayList<TickerSource> sources;
	
	private Double lastPlayedTick;
	private Double nextTick;
	
	private boolean ready;
	
	public Ticker() {
		sources = new ArrayList<>();
		lastPlayedTick = null;
		nextTick = null;
		ready = false;
	}
	
	public void addSource(TickerSource source) {
		if (!sources.contains(source)) {
			sources.add(source);
			ready = false;
		}
	}
	
	public void removeSource(TickerSource source) {
		sources.remove(source);
		ready = false;
	}
	
	public void removeAllSources() {
		sources.clear();
	}
	
	public void reset(double startPos) {
		lastPlayedTick = null;
		nextTick = computeNextTickInclusive(startPos);
		ready = true;
	}
	
	public void resetIfNotReady(double startPos) {
		if (!ready) {
			reset(startPos);
		}
	}
	
	public Double getNextTick() {
		if (!ready) {
			throw new IllegalStateException("Not ready");
		}
		
		return nextTick;
	}
	
	public void markNextTickPlayed() {
		if (!ready) {
			throw new IllegalStateException("Not ready");
		}
		
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
