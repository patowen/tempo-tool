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
	private int length;
	private int numChannels;
	private int bytesPerSample;
	private float samplingRate;
	private ByteBuffer totalBuffer;
	private ByteBuffer fragmentBuffer;
	private int currentSample;
	private double currentSubsample;
	private boolean playing;
	private double playSpeed;
	
	private int initialFramePosition;
	private int initialSample;
	
	private SourceDataLine line;
	
	private ActionListener audioStreamUpdater;
	private Timer updateTimer;
	
	private Ticker ticker;
	private boolean hasCurrentTick;
	private int currentTickSample;
	
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
		
		audioStreamUpdater = new ActionListener() {
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
