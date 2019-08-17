package net.patowen.songanalyzer;

import java.util.TreeSet;

public class MackDataMarker implements MackData {
	private TreeSet<Double> marks;
	
	public MackDataMarker() {
		this.marks = new TreeSet<>();
	}
	
	public TreeSet<Double> getMarks() {
		return marks;
	}
	
	@Override
	public int getType() {
		return 1;
	}
}
