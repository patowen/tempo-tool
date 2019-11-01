package net.patowen.songanalyzer.old;

import javax.swing.JComponent;

import net.patowen.songanalyzer.AudioStream;
import net.patowen.songanalyzer.deck.TrackBounds;
import net.patowen.songanalyzer.undo.UserActionList;

public class TrackStatus {
	private JComponent refreshComponent;
	
	public AudioStream audioStream;
	public TrackBounds bounds;
	public double playBarPos;
	
	public UserActionList userActionList;
	
	public TrackStatus(JComponent refreshComponent, AudioStream audioStream) {
		this.refreshComponent = refreshComponent;
		this.audioStream = audioStream;
		this.bounds = new TrackBounds(0, 60);
		this.playBarPos = 0;
		this.userActionList = new UserActionList();
	}
	
	public void updatePlayBar() {
		playBarPos = audioStream.getPos();
	}
	
	public void refresh() {
		refreshComponent.repaint();
	}
}
