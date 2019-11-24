/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.patowen.tempotool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioInputStream;
import javax.swing.SwingUtilities;

public class SoundFileLoadingThread extends Thread {
	private final AudioInputStream audioInputStream;
	private final FragmentedByteList totalBuffer;
	private Status status;
	
	private byte[] tempBuffer;
	private int tempBufferLength;
	
	public enum Status {
		PENDING,
		DONE,
		ERROR
	}
	
	public SoundFileLoadingThread(AudioInputStream audioInputStream, FragmentedByteList totalBuffer) {
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
