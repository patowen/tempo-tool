package net.patowen.songanalyzer;

public interface AudioStream {
	public void setPos(double pos);
	public void play(double speed);
	public void pause();
	public double getPos();
	public boolean isPlaying(); // Will the pos change in the near future?
	public double getLength();
	public Ticker getTicker();
}
