package net.patowen.songanalyzer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.swing.SwingUtilities;

public class SoundFileLoadingThread extends Thread {
	private final AudioInputStream audioInputStream;
	private final ArrayList<Byte> totalBuffer;
	private Status status;
	
	private byte[] tempBuffer;
	private int tempBufferLength;
	
	public enum Status {
		PENDING,
		DONE,
		ERROR
	}
	
	public SoundFileLoadingThread(AudioInputStream audioInputStream, ArrayList<Byte> totalBuffer) {
		this.audioInputStream = audioInputStream;
		this.totalBuffer = totalBuffer;
		tempBuffer = new byte[4096];
		status = Status.PENDING;
		setDaemon(true);
	}
	
	private void loadSoundFile(AudioInputStream audioInputStream) throws IOException, InvocationTargetException, InterruptedException {
		tempBufferLength = 0;
		while (tempBufferLength != -1 && !Thread.interrupted()) {
			tempBufferLength = audioInputStream.read(tempBuffer, 0, tempBuffer.length);
			if (tempBufferLength != -1) {
				SwingUtilities.invokeAndWait(() -> {
					for (int i=0; i<tempBufferLength; i++) {
						totalBuffer.add(tempBuffer[i]);
					}
				});
			}
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	@Override
	public void run() {
		try {
			loadSoundFile(audioInputStream);
			SwingUtilities.invokeLater(() -> { status = Status.DONE; });
		} catch (InterruptedException e) {
			return;
		} catch (InvocationTargetException | IOException e) {
			e.printStackTrace();
			SwingUtilities.invokeLater(() -> { status = Status.ERROR; });
		}
	}
}
