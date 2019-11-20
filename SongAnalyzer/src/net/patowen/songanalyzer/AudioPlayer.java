package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.patowen.songanalyzer.Config.Keys;
import net.patowen.songanalyzer.DialogManager.DialogKind;
import net.patowen.songanalyzer.SoundFileLoadingThread.Status;

public class AudioPlayer {
	private final AnimationSource playingAnimation;
	private final AnimationSource bufferingAnimation;
	private final Ticker ticker;
	
	private Path audioFile;
	private AudioStream audioStream;
	private double speed;
	
	public AudioPlayer(AnimationController animationController, Ticker ticker) {
		playingAnimation = animationController.createAnimationSource();
		bufferingAnimation = animationController.createAnimationSource();
		this.ticker = ticker;
		
		switchToBlankAudioStream();
		
		audioFile = null;
		speed = 1;
	}
	
	public void destroy() {
		if (audioStream != null) {
			audioStream.destroy();
		}
		playingAnimation.stop();
		bufferingAnimation.stop();
	}
	
	public void pollBufferingStatus() {
		if (audioStream != null) {
			if (audioStream.getLoadingStatus() != Status.PENDING) {
				bufferingAnimation.stop();
			}
		}
	}
	
	public void reset() {
		switchToBlankAudioStream();
		
		playingAnimation.stop();
		bufferingAnimation.stop();
		
		audioFile = null;
		speed = 1;
	}
	
	private void switchToBlankAudioStream() {
		if (audioStream != null) {
			audioStream.destroy();
		}
		
		// We ideally want to be able to play tick sounds, but don't panic until a file is loaded and playing audio
		// is still impossible.
		try {
			audioStream = new AudioStream(null, this.ticker);
		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
			e.printStackTrace();
			audioStream = null;
		}
	}
	
	public void resetTicker() {
		if (audioStream != null) {
			audioStream.resetTicker();
		}
	}
	
	private void setAudioFile(Path audioFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		switchToBlankAudioStream();
		this.audioFile = null;
		
		audioStream = new AudioStream(audioFile.toFile(), ticker);
		this.audioFile = audioFile;
		bufferingAnimation.start();
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
				playingAnimation.start();
			} else {
				audioStream.pause();
				playingAnimation.stop();
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
			return audioStream.getPos() + AnimationController.timerDelayMilliseconds * 0.5 * 0.001;
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
	
	public void visualize(Graphics2D g, int width, int height, double startTime, double timeLength) {
		if (audioStream != null) {
			audioStream.visualize(g, width, height, startTime, timeLength);
		}
	}
	
	public boolean hasAudioStream() {
		return audioStream != null;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void chooseAudioFileFromUser(Config config, DialogManager fileDialogManager) {
		Path path = fileDialogManager.getUserChosenPath(
				config.getConfigEntryPath(Keys.DEFAULT_SONG_FOLDER),
				getAudioFile(),
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
	
	public void loadAudioFileFromSave(Path path, DialogManager dialogManager) {
		try {
			setAudioFile(path);
		} catch (IOException e) {
			dialogManager.showCustomErrorDialog(path, "Could not open the audio file attached to the current save. Does it still exist?");
		} catch (UnsupportedAudioFileException e) {
			dialogManager.showFileFormatErrorDialog(path, "The audio file attached to the current save is not a recognized audio file.");
		} catch (LineUnavailableException e) {
			dialogManager.showCustomErrorDialog(path, "Cannot get the necessary resources to play audio. Perhaps too many applications are using audio.");
		}
	}
}
