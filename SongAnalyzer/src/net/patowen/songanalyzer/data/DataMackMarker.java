package net.patowen.songanalyzer.data;

import java.util.ArrayList;
import java.util.List;

import net.patowen.songanalyzer.data.general.Arr;
import net.patowen.songanalyzer.data.general.Dict;
import net.patowen.songanalyzer.data.general.FileFormatException;
import net.patowen.songanalyzer.data.general.Obj;

public class DataMackMarker extends Dict {
	private interface Keys {
		byte marks = 0;
	}
	
	public List<Double> getMarks() throws FileFormatException {
		List<Obj> markObjs = get(Keys.marks).asArr().get();
		List<Double> marks = new ArrayList<Double>();
		for (Obj obj : markObjs) {
			marks.add(obj.asDouble());
		}
		return marks;
	}
	
	public void setMarks(List<Double> marks) {
		Arr arr = new Arr();
		for (double mark : marks) {
			arr.add(mark);
		}
		set(Keys.marks, arr);
	}
}
