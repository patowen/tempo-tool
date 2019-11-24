package net.patowen.tempotool;

public interface TickerSource {
	public Double getNextTickInclusive(double pos);
	public Double getNextTickExclusive(double pos);
}
