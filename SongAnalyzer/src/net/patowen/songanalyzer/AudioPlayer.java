package net.patowen.songanalyzer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import net.patowen.songanalyzer.Config.Keys;
import net.patowen.songanalyzer.DialogManager.DialogKind;

public class AudioPlayer {
	private Component component;
	private Path audioFile;
	private AudioStream audioStream;
	private double speed;
	
	private int timerDelayMilliseconds;
	private Timer playbarTimer;
	
	public AudioPlayer(Component component) {
		this.component = component;
		audioStream = null;
		audioFile = null;
		speed = 1;
		timerDelayMilliseconds = 30;
		playbarTimer = new Timer(timerDelayMilliseconds, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AudioPlayer.this.component.repaint();
			}
		});
	}
	
	public void destroy() {
		if (audioStream != null) {
			audioStream.destroy();
		}
		playbarTimer.stop();
	}
	
	private void setAudioFile(Path audioFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		if (audioStream != null) {
			audioStream.destroy();
			audioStream = null;
		}
		
		this.audioFile = null;
		
		audioStream = new AudioStream(audioFile.toFile());
		this.audioFile = audioFile;
	}
	
	public Path getAudioFile() {
		return audioFile;
	}
	
	public boolean isPlaying() {
		return audioStream != null && audioStream.isPlaying();
	}
	
	public void setPlaying(boolean playing) {
		if (audioStream != null) {
			if (playing) {
				audioStream.play(speed);
				playbarTimer.restart();
			} else {
				audioStream.pause();
				playbarTimer.stop();
			}
		}
	}
	
	public void setPos(double pos) {
		if (audioStream == null) {
			return;
		}
		
		audioStream.setPos(pos);
	}
	
	public double getPos() {
		if (audioStream == null) {
			return 0;
		}
		
		if (audioStream.isPlaying()) {
			return audioStream.getPos() + timerDelayMilliseconds * 0.5 * 0.001;
		} else {
			return audioStream.getPos();
		}
	}
	
	public double getLength() {
		if (audioStream == null) {
			return 0;
		}
		return audioStream.getLength();
	}
	
	public boolean hasAudioStream() {
		return audioStream != null;
	}
	
	public void chooseAudioFileFromUser(Config config, DialogManager fileDialogManager) {
		Path path = fileDialogManager.getUserChosenPath(
				config.getConfigEntryPath(Keys.DEFAULT_SONG_FOLDER),
				"Supported sound files",
				new String[] {"wav", "mp3"},
				DialogKind.OPEN);
		
		if (path == null) {
			return;
		}
		
		try {
			setAudioFile(path);
			config.setConfigEntryPath(Keys.DEFAULT_SONG_FOLDER, path.getParent());
		} catch (IOException e) {
			fileDialogManager.showErrorDialog(path, DialogKind.OPEN);
		} catch (UnsupportedAudioFileException e) {
			fileDialogManager.showFileFormatErrorDialog(path, "Not a recognized audio file. Try converting it in a sound editor.");
		} catch (LineUnavailableException e) {
			fileDialogManager.showCustomErrorDialog(path, "Cannot get the necessary resources to play audio. Perhaps too many applications are using audio.");
		}
	}
}
