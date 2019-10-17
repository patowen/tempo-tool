package net.patowen.songanalyzer;

public class TrackBounds {
	private double width;
	private double totalSeconds;
	private double secondsStart;
	
	public TrackBounds(double secondsStart, double totalSeconds) {
		this.secondsStart = secondsStart;
		this.totalSeconds = totalSeconds;
	}
	
	public double secondsToSubpixel(double seconds) {
		return (seconds - secondsStart) * (width / totalSeconds);
	}
	
	public int secondsToPixel(double seconds) {
		return (int)Math.floor(secondsToSubpixel(seconds));
	}
	
	public double subpixelToSeconds(double subPixel) {
		return subPixel * (totalSeconds / width) + secondsStart;
	}
	
	public double pixelToSeconds(int pixel) {
		return subpixelToSeconds(pixel + 0.5);
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void zoom(double seconds, double factor) {
		secondsStart = (secondsStart - seconds) * factor + seconds;
		totalSeconds *= factor;
	}
	
	public void setBounds(double secondsStart, double totalSeconds) {
		this.secondsStart = secondsStart;
		this.totalSeconds = totalSeconds;
	}
}
