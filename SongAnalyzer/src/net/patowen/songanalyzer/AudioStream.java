package net.patowen.songanalyzer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;

import net.patowen.songanalyzer.old.Ticker;

// Anyone interested in some spaghetti?
public class AudioStream {
	private int length; // Length of the steam in samples. Samples are usually 1/44100 seconds no matter how many channels there are.
	private int numChannels; // Usually 2 for stereo
	private int bytesPerSample; // Number of bytes in the byte buffer for each sample
	private float samplingRate; // Usually 44100
	private ByteBuffer totalBuffer; // Contains uncompressed audio for the entire loaded sound file. Playing cannot begin until the entire sound file is loaded into memory.
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
	
	private void playFragment() {
		fragmentBuffer.clear();
		
		int samplesThisPass = line.available() / bytesPerSample;
		
		double currentPos = getCurrentSampleInSeconds();
		Double nextTickPos = ticker.getNextTick(currentPos);
		
		for (int i=0; i < samplesThisPass; i++) {
			currentPos = getCurrentSampleInSeconds();
			if (nextTickPos != null && currentPos >= nextTickPos) {
				hasCurrentTick = true;
				currentTickSample = 0;
				ticker.markNextTickPlayed();
				nextTickPos = ticker.getNextTick();
			}
			
			for (int j=0; j < numChannels; j++) {
				double totalAmp = getAmpInterpolated(currentSample, currentSubsample, j) * 0.4;
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
			currentTickSample ++;
		}
		
		line.write(fragmentBuffer.array(), 0, fragmentBuffer.position());
	}
	
	private double getCurrentSampleInSeconds() {
		return ((double)currentSample + currentSubsample) / (double)samplingRate;
	}
	
	public AudioStream(File file) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		AudioFormat baseFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat
				(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
						baseFormat.getChannels(), baseFormat.getChannels()*2, baseFormat.getSampleRate(), false);
		AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);

		numChannels = decodedFormat.getChannels();
		samplingRate = decodedFormat.getSampleRate();
		
		ArrayList<Byte> dataList = new ArrayList<Byte>(65536);
		byte[] data = new byte[4096];
		int framesRead = 0;
		while (framesRead != -1) {
			framesRead = din.read(data, 0, data.length);
			if (framesRead != -1) {
				for (int i=0; i<framesRead; i++) {
					dataList.add(data[i]);
				}
			}
		}
		
		totalBuffer = ByteBuffer.allocate(dataList.size()).order(ByteOrder.LITTLE_ENDIAN);
		for (int i=0; i<dataList.size(); i++) {
			totalBuffer.put(dataList.get(i));
		}
		
		totalBuffer.rewind();
		bytesPerSample = 2 * numChannels;
		length = dataList.size() / bytesPerSample;
		
		AudioFormat format = new AudioFormat(samplingRate, 16, numChannels, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine)AudioSystem.getLine(info);
		line.open(format);
		
		fragmentBuffer = ByteBuffer.allocate(line.getBufferSize());	
		currentSample = 0;
		playing = false;
		playSpeed = 1;
		
		ActionListener audioStreamUpdater = new ActionListener() {
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
		
		ticker = new Ticker();
		hasCurrentTick = false;
	}
	
	public void destroy() {
		updateTimer.stop();
		line.close();
	}
	
	private short getAmpRaw(int index, int channel) {
		if (index >= length || index < 0) return 0;
		return totalBuffer.getShort((index*numChannels+channel)*2);
	}
	
	private double getAmpInterpolated(int index, double subIndex, int channel) {
		short amp0 = getAmpRaw(index, channel);
		short amp1 = getAmpRaw(index + 1, channel);
		return amp0 + ((amp1 - amp0) * subIndex);
	}
	
	private double getTickAmp(int index) {
		double pos = (double)index / (double)samplingRate;
		if (pos > 0 && pos < 0.01) {
			return Math.sin(Math.PI * 2 * 800 * pos) * Short.MAX_VALUE;
		}
		return 0;
	}
	
	public double getLength() {
		return (double)length / (double)samplingRate;
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
		ticker.reset(getCurrentSampleInSeconds());
		initialSample = currentSample;
		initialFramePosition = line.getFramePosition();
		line.start();
		updateTimer.restart();
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
	
	public Ticker getTicker() {
		return ticker;
	}
}
