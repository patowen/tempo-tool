package net.patowen.songanalyzer;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SongAnalyzerRunner {
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		AudioStreamFile stream = new AudioStreamFile(new File("C:\\Users\\Patrick\\Desktop\\test.mp3"));
		System.gc();
		new TrackView(stream);
	}
}
