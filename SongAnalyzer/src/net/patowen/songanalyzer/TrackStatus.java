package net.patowen.songanalyzer;

import javax.swing.JComponent;

public class TrackStatus {
	private JComponent refreshComponent;
	
	public AudioStream audioStream;
	public TrackBounds bounds;
	public double playBarPos;
	
	public TrackStatus(JComponent refreshComponent, AudioStream audioStream) {
		this.refreshComponent = refreshComponent;
		this.audioStream = audioStream;
		this.bounds = new TrackBounds(0, 60);
		this.playBarPos = 0;
	}
	
	public void updatePlayBar() {
		playBarPos = audioStream.getPos();
	}
	
	public void refresh() {
		refreshComponent.repaint();
	}
}
