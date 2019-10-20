package net.patowen.songanalyzer;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {
	private Path audioFile;
	private AudioStream audioStream;
	
	public AudioPlayer() {
		audioStream = null;
		audioFile = null;
	}
	
	public void setAudioFile(Path audioFile) {
		this.audioFile = audioFile;
		
		try {
			audioStream = new AudioStream(audioFile.toFile());
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			this.audioFile = null;
			this.audioStream = null;
		}
	}
	
	public Path getAudioFile() {
		return audioFile;
	}
	
	public void setPos(double pos) {
		if (audioStream == null) {
			return;
		}
		
		audioStream.setPos(pos);
	}
	
	public double getPos(double avgDelay) {
		if (audioStream == null) {
			return 0;
		}
		
		if (audioStream.isPlaying()) {
			return audioStream.getPos() + avgDelay;
		} else {
			return audioStream.getPos();
		}
	}
	
	public boolean hasAudioStream() {
		return audioStream != null;
	}
}
