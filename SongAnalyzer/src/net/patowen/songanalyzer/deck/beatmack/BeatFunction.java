package net.patowen.songanalyzer.deck.beatmack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.patowen.songanalyzer.data.Arr;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.data.Obj;
import net.patowen.songanalyzer.deck.beatmack.Spline.KnotType;

public class BeatFunction {
	private Spline spline;
	private final TreeMap<Double, Knot> knots;
	
	public BeatFunction() {
		knots = new TreeMap<>();
		
		Knot startKnot = new Knot();
		startKnot.time = 0;
		startKnot.phase = 0;
		
		startKnot.regionBefore = Region.cubic;
		startKnot.regionAfter = Region.cubic;
		
		Knot laterKnot = new Knot();
		laterKnot.time = 8;
		laterKnot.phase = 8;
		
		laterKnot.regionBefore = startKnot.regionAfter;
		laterKnot.regionAfter = Region.cubic;
		
		knots.put(startKnot.time, startKnot);
		knots.put(laterKnot.time, laterKnot);
		
		createSpline();
	}
	
	public Knot getKnotOnBeat(double time) {
		double phase = Math.floor(getPhaseFromTime(time) + 0.5);
		Knot newKnot = new Knot();
		newKnot.time = time;
		newKnot.phase = phase;
		
		Knot previousKnot = getKnotFromEntry(knots.floorEntry(time));
		Knot nextKnot = getKnotFromEntry(knots.ceilingEntry(time));
		int region;
		if (nextKnot == null) {
			region = previousKnot.regionAfter;
		} else {
			region = nextKnot.regionBefore;
		}
		
		int[] newRegions = splitRegion(region);
		
		newKnot.regionBefore = newRegions[0];
		newKnot.regionAfter = newRegions[1];
		
		return newKnot;
	}
	
	public void insertKnot(Knot knot) {
		knots.put(knot.time, knot);
		Knot previousKnot = getKnotFromEntry(knots.lowerEntry(knot.time));
		Knot nextKnot = getKnotFromEntry(knots.ceilingEntry(knot.time));
		if (previousKnot != null) {
			previousKnot.regionAfter = knot.regionBefore;
		}
		if (nextKnot != null) {
			nextKnot.regionBefore = knot.regionAfter;
		}
		
		createSpline();
	}
	
	public boolean canDeleteKnot(Knot knot) {
		return knots.size() > 2;
	}
	
	public boolean regionHasPhaseDisplacement(double time) {
		return (!knots.containsKey(time) && time > knots.firstKey() && time < knots.lastKey());
	}
	
	public double getRegionPhaseDisplacement(double time) {
		return knots.higherEntry(time).getValue().phase - knots.lowerEntry(time).getValue().phase;
	}
	
	public void setRegionPhaseDisplacement(double time, double phaseDisplacement) {
		double phaseDisplacementDelta = phaseDisplacement - getRegionPhaseDisplacement(time);
		for (Knot knot : knots.tailMap(time).values()) {
			knot.phase += phaseDisplacementDelta;
		}
		
		updateSpline();
	}
	
	public void deleteKnot(Knot knot) {
		Knot previousKnot = getKnotFromEntry(knots.lowerEntry(knot.time));
		Knot nextKnot = getKnotFromEntry(knots.higherEntry(knot.time));
		
		int mergedRegion = mergeRegions(knot.regionBefore, knot.regionAfter);
		if (previousKnot != null) {
			previousKnot.regionAfter = mergedRegion;
		}
		
		if (nextKnot != null) {
			nextKnot.regionBefore = mergedRegion;
		}
		
		knots.remove(knot.time);
		
		createSpline();
	}
	
	public void moveKnot(Knot knot, double time) {
		knots.remove(knot.time);
		
		if (knots.floorKey(knot.time) != knots.floorKey(time)) {
			// Can't move a knot past another knot
			knots.put(knot.time, knot);
			return;
		}
		
		knot.time = time;
		knots.put(knot.time, knot);
		
		updateSpline();
	}
	
	public Knot findClosestKnot(double time) {
		Double lower = knots.floorKey(time);
		Double upper = knots.ceilingKey(time);
		
		if (lower == null && upper == null) {
			return null;
		}
		
		if (lower == null) {
			return knots.get(upper);
		}
		
		if (upper == null) {
			return knots.get(lower);
		}
		
		return knots.get(upper - time < time - lower ? upper : lower);
	}
	
	public Iterable<Knot> getAllKnots() {
		return knots.values();
	}
	
	public Double findTimeForClosestBeat(double time) {
		double phase = getPhaseFromTime(time);
		double lowerPhase = Math.floor(phase);
		double upperPhase = lowerPhase + 1;
		double lowerTime = getTimeFromPhase(lowerPhase, time);
		double upperTime = getTimeFromPhase(upperPhase, time);
		
		return upperTime - time < time - lowerTime ? upperTime : lowerTime;
	}
	
	public Double findTimeForNextBeat(double time) {
		double phase = getPhaseFromTime(time + 1e-9);
		double goalPhase = Math.floor(phase + 1);
		return getTimeFromPhase(goalPhase, time);
	}
	
	public double getPhaseFromTime(double time) {
		return spline.eval(time);
	}
	
	public double getTempoFromTime(double time) {
		return spline.derivative(time);
	}
	
	private Double getTimeFromPhase(double phase, double guess) {
		return spline.invEval(phase, guess);
	}
	
	private void createSpline() {
		ArrayList<KnotType> splineKnots = new ArrayList<>();
		int splineIndex = 0;
		
		for (Knot knot : knots.values()) {
			knot.splineIndex = splineIndex;
			
			KnotType knotType;
			if (knots.firstEntry().getValue() == knot) {
				knotType = KnotType.ConformToLater;
			} else if (knots.lastEntry().getValue() == knot) {
				knotType = KnotType.ConformToEarlier;
			} else {
				knotType = KnotType.NonDifferentiable;
			}
			
			splineKnots.add(knotType);
			splineIndex++;
		}
		
		spline = new Spline(splineKnots);
		updateSpline();
	}
	
	private void updateSpline() {
		for (Knot knot : knots.values()) {
			int i = knot.splineIndex;
			spline.x[i] = knot.time;
			spline.y[i] = knot.phase;
		}
		
		spline.computeSpline();
	}
	
	private Knot getKnotFromEntry(Map.Entry<Double, Knot> entry) {
		if (entry == null) {
			return null;
		}
		return entry.getValue();
	}
	
	// Splitting followed by merging regions should be a no-op, or undo/redo will be broken.
	private int[] splitRegion(int region) {
		return new int[] {region, region};
	}
	
	private int mergeRegions(int regionBefore, int regionAfter) {
		if (regionBefore == Region.linear || regionAfter == Region.linear) {
			return Region.linear;
		}
		return Region.cubic;
	}
	
	private interface Keys {
		int knots = 0;
		int regions = 1;
	}
	
	private interface KnotKeys {
		int time = 0;
		int phase = 1;
	}
	
	private interface RegionKeys {
		int type = 0;
	}
	
	public void save(Dict dict) {
		Arr knotArr = new Arr();
		Arr regionArr = new Arr();
		
		for (Knot knot : knots.values()) {
			Dict knotDict = new Dict();
			knotDict.set(KnotKeys.time, knot.time);
			knotDict.set(KnotKeys.phase, knot.phase);
			knotArr.add(knotDict);
			
			int region = knot.regionBefore;
			Dict regionDict = new Dict();
			regionDict.set(RegionKeys.type, region);
			regionArr.add(regionDict);
		}
		
		int finalRegion = knots.lastEntry().getValue().regionAfter;
		Dict finalRegionDict = new Dict();
		finalRegionDict.set(RegionKeys.type, finalRegion);
		regionArr.add(finalRegionDict);
		
		dict.set(Keys.knots, knotArr);
		dict.set(Keys.regions, regionArr);
	}
	
	public void load(Dict dict) throws FileFormatException {
		knots.clear();
		
		List<Obj> knotArr = dict.get(Keys.knots).asArr().get();
		List<Obj> regionArr = dict.get(Keys.regions).asArr().get();
		
		int[] regions = new int[regionArr.size()];
		for (int i=0; i<regions.length; i++) {
			Dict regionDict = regionArr.get(i).asDict();
			regions[i] = regionDict.get(RegionKeys.type).asInt();
		}
		
		for (int i=0; i<knotArr.size(); i++) {
			Dict knotDict = knotArr.get(i).asDict();
			Knot knot = new Knot();
			knot.time = knotDict.get(KnotKeys.time).asDouble();
			knot.phase = knotDict.get(KnotKeys.phase).asDouble();
			knot.regionBefore = regions[i];
			knot.regionAfter = regions[i+1];
			knots.put(knot.time, knot);
		}
		
		createSpline();
	}
	
	public static class Knot {
		private double time;
		private double phase;
		
		private int regionBefore;
		private int regionAfter;
		
		private int splineIndex;
		
		public double getTime() {
			return time;
		}
	}
	
	private interface Region {
		int cubic = 0;
		int linear = 1;
	}
}
