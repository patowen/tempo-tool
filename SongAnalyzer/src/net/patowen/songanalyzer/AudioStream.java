package net.patowen.songanalyzer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import org.tritonus.share.sampled.AudioFormats;

import net.patowen.songanalyzer.SoundFileLoadingThread.Status;

// Anyone interested in some spaghetti?
public class AudioStream {
	private int numChannels; // Usually 2 for stereo
	private int bytesPerSample; // Number of bytes in the byte buffer for each sample
	private float samplingRate; // Usually 44100
	private FragmentedByteList totalBuffer; // Contains uncompressed audio for the entire loaded sound file. Playing cannot begin until the entire sound file is loaded into memory.
	private ByteBuffer fragmentBuffer; // Contains the audio that is about to be sent to the OS to play. Includes data from the totalBuffer, possibly slowed down, along with possible metronome ticks. Updated frequently.
	private int currentSample; // Current position in the totalBuffer. When audio is paused, it is where it will start playing again. When audio is playing, it advances based on what has already been sent to the SourceDataLine, but it will be ahead of what has actually played and cannot be used directly for the play bar.
	private double currentSubsample; // To allow sound to be played slowed down, fractional samples are allowed, and the fractional part is separated out.
	private boolean playing; // This is the main indicator that communicates whether the audio is playing or paused.
	private double playSpeed; // This is set externally to allow sound to be played slowed down.
	
	private int initialFramePosition; // The frame position after a transition from pausing to playing, used to calculate the actual current sample while playing.
	private int initialSample; // The value of currentSample after a transition from pausing to playing, used to calculate the actual current sample while playing.
	
	private SourceDataLine line; // The actual line used that allows sound to physically come out of the speakers.
	
	private Timer updateTimer; // This timer is used to feed audio data in the UI thread to avoid needing to worry about multithreading.
	
	private Ticker ticker; // The source that decides when to play a tick sound
	private boolean hasCurrentTick; // Whether a metronome tick sound is currently playing
	private int currentTickSample; // The current progress along the waveform of the metronome tick sound
	
	private SoundFileLoadingThread soundFileLoadingThread;
	
	private void playFragment() {
		fragmentBuffer.clear();
		
		int numSamples = getNumSamples();
		
		int samplesThisPass = line.available() / bytesPerSample;
		
		double currentPos = getCurrentSampleInSeconds();
		ticker.resetIfNotReady(currentPos);
		Double nextTickPos = ticker.getNextTick();
		
		for (int i=0; i < samplesThisPass; i++) {
			currentPos = getCurrentSampleInSeconds();
			if (nextTickPos != null && currentPos >= nextTickPos) {
				hasCurrentTick = true;
				currentTickSample = 0;
				ticker.markNextTickPlayed();
				nextTickPos = ticker.getNextTick();
			}
			
			for (int j=0; j < numChannels; j++) {
				double totalAmp = getAmpInterpolated(numSamples, currentSample, currentSubsample, j) * 0.4;
				if (hasCurrentTick) {
					totalAmp += getTickAmp(currentTickSample) * 0.4;
				}
				fragmentBuffer.putShort((short)totalAmp);
			}
			currentSubsample += playSpeed;
			while (currentSubsample >= 1) {
				currentSubsample -= 1;
				currentSample ++;
			}
			while (currentSubsample < 0) {
				currentSubsample += 1;
				currentSample --;
			}
			currentTickSample ++;
		}
		
		line.write(fragmentBuffer.array(), 0, fragmentBuffer.position());
	}
	
	private double getCurrentSampleInSeconds() {
		return ((double)currentSample + currentSubsample) / (double)samplingRate;
	}
	
	public AudioStream(File file, Ticker ticker) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		totalBuffer = new FragmentedByteList(65536);
		
		ActionListener audioStreamUpdater = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playing) {
					if (line.available() > line.getBufferSize() / 2) {
						playFragment();
					}
				}
			}
		};
		
		updateTimer = new Timer(0, audioStreamUpdater);
		updateTimer.setDelay(100);
		
		this.ticker = ticker;
		hasCurrentTick = false;
		
		if (file == null) {
			loadFromNothing();
		} else {
			loadFromFile(file);
		}
	}
	
	private void loadFromNothing() throws LineUnavailableException {
		numChannels = 1;
		samplingRate = 44100;
		
		initAudioLine();
	}
	
	private void loadFromFile(File file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		AudioFormat baseFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels()*2,
				baseFormat.getSampleRate(),
				false);
		AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);

		numChannels = decodedFormat.getChannels();
		samplingRate = decodedFormat.getSampleRate();
		
		soundFileLoadingThread = new SoundFileLoadingThread(din, totalBuffer);
		soundFileLoadingThread.start();
		
		initAudioLine();
	}
	
	private void initAudioLine() throws LineUnavailableException {
		bytesPerSample = 2 * numChannels;

		AudioFormat format = new AudioFormat(samplingRate, 16, numChannels, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine)AudioSystem.getLine(info);
		line.open(format);
		
		fragmentBuffer = ByteBuffer.allocate(line.getBufferSize());	
		
		currentSample = 0;
		playing = false;
		playSpeed = 1;
	}
	
	public void destroy() {
		if (soundFileLoadingThread != null) {
			soundFileLoadingThread.interrupt();
			try {
				soundFileLoadingThread.join();
			} catch (InterruptedException e) {
				// The UI thread shouldn't be interrupted.
			}
		}
		
		updateTimer.stop();
		line.close();
	}
	
	public SoundFileLoadingThread.Status getLoadingStatus() {
		if (soundFileLoadingThread == null) {
			return Status.DONE;
		}
		
		return soundFileLoadingThread.getStatus();
	}
	
	private int getNumSamples() {
		return totalBuffer.size() / bytesPerSample;
	}
	
	private double getAmpRaw(int numSamples, int index, int channel) {
		if (index >= numSamples || index < 0) return 0;
		int totalBufferPos = (index*numChannels+channel)*2;
		return Byte.toUnsignedInt(totalBuffer.get(totalBufferPos)) + totalBuffer.get(totalBufferPos + 1) * 256;
	}
	
	private double getAmpInterpolated(int numSamples, int index, double subIndex, int channel) {
		double amp0 = getAmpRaw(numSamples, index, channel);
		double amp1 = getAmpRaw(numSamples, index + 1, channel);
		return amp0 + ((amp1 - amp0) * subIndex);
	}
	
	private double getTickAmp(int index) {
		double pos = (double)index / (double)samplingRate;
		if (pos > 0 && pos < 0.01) {
			return Math.sin(Math.PI * 2 * 1000 * pos) * Math.exp(-pos*100) * Short.MAX_VALUE;
		}
		return 0;
	}
	
	public double getLength() {
		return (double)getNumSamples() / (double)samplingRate;
	}

	public void setPos(double pos) {
		boolean shouldPlay = playing;
		
		if (playing) {
			pause();
		}
		
		currentSample = (int)(pos * samplingRate);
		currentSubsample = 0;
		
		if (shouldPlay) {
			play(playSpeed);
		}
	}

	public void play(double speed) {
		if (playing) {
			pause();
		}
		
		playing = true;
		playSpeed = speed;
		resetTicker();
		initialSample = currentSample;
		initialFramePosition = line.getFramePosition();
		line.start();
		updateTimer.restart();
	}
	
	public void resetTicker() {
		ticker.reset(getCurrentSampleInSeconds());
	}

	public void pause() {
		if (playing) {
			updateTimer.stop();
			
			line.stop();
			int frameProgress = line.getFramePosition() - initialFramePosition;
			currentSample = initialSample + (int)(frameProgress * playSpeed);
			line.flush();
			
			playing = false;
		}
	}

	public double getPos() {
		int computedSample;
		if (playing) {
			int frameProgress = line.getFramePosition() - initialFramePosition;
			computedSample = initialSample + (int)(frameProgress * playSpeed);
		} else {
			computedSample = currentSample;
		}
		return (double)computedSample / (double)samplingRate;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void visualize(Graphics2D g, int width, int height, double startTime, double timeLength) {
		double samplesPerPixel = (double)samplingRate * timeLength / (double)width;
		if (samplesPerPixel > 200) {
			double startSubpixel = (0.0 - startTime) / timeLength * (double)width;
			double endSubpixel = (getLength() - startTime) / timeLength * (double)width;
			int startPixel = Math.max((int)Math.floor(startSubpixel + 0.5), 0);
			int endPixel = Math.min((int)Math.floor(endSubpixel + 0.5), width);

			g.setColor(new Color(128, 128, 128));
			g.fillRect(startPixel, 8, endPixel - startPixel, height - 16);
		} else {
			int startSample = (int)Math.floor(startTime * samplingRate);
			int endSample = (int)Math.ceil((startTime + timeLength) * samplingRate);
			
			int xPrevious = 0, yPrevious = 0;
			int numSamples = getNumSamples();
			g.setColor(Color.WHITE);
			for (int sample = startSample; sample <= endSample; sample++) {
				int x = (int)Math.floor((double) (sample - startSample) / (double) samplingRate / timeLength * width);
				double totalAmp = 0;
				for (int i=0; i<numChannels; i++) {
					totalAmp += getAmpRaw(numSamples, sample, i);
				}
				int y = (int)Math.floor(-(totalAmp * (double)height) / (32768.0 * (double)numChannels * 2) + ((double)height / 2.0));
				
				if (sample != startSample) {
					g.drawLine(xPrevious, yPrevious, x, y);
				}
				xPrevious = x;
				yPrevious = y;
			}
		}
	}
}
