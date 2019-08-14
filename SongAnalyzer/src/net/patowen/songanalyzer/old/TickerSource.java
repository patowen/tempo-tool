package net.patowen.songanalyzer.old;

public interface TickerSource {
	public Double getNextTickInclusive(double pos);
	public Double getNextTickExclusive(double pos);
}
