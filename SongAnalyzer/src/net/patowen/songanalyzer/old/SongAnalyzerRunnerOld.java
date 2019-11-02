package net.patowen.songanalyzer.old;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.patowen.songanalyzer.AudioStream;

public class SongAnalyzerRunnerOld {
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		AudioStream stream = new AudioStream(new File("C:\\Users\\Patrick\\Desktop\\test.wav"), null);
		System.gc();
		new TrackView(stream);
	}
}
