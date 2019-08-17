package net.patowen.songanalyzer;

import java.util.TreeSet;

// TODO: Remove
public class MackDataMarker {
	private TreeSet<Double> marks;
	
	public MackDataMarker() {
		this.marks = new TreeSet<>();
	}
	
	public TreeSet<Double> getMarks() {
		return marks;
	}
	
	public int getType() {
		return 1;
	}
}
