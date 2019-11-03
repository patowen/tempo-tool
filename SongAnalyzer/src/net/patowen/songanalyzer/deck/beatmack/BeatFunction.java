package net.patowen.songanalyzer.deck.beatmack;

import java.util.Map;
import java.util.TreeMap;

public class BeatFunction {
	private Spline spline;
	private final TreeMap<Double, Knot> knots;
	
	public BeatFunction() {
		knots = new TreeMap<>();
		
		Knot startKnot = new Knot();
		startKnot.time = 0;
		startKnot.phase = 0;
		
		startKnot.regionBefore = new CubicRegion();
		startKnot.regionAfter = new CubicRegion();
		
		Knot laterKnot = new Knot();
		laterKnot.time = 8;
		laterKnot.phase = 8;
		
		laterKnot.regionBefore = startKnot.regionAfter;
		laterKnot.regionAfter = new CubicRegion();
		
		knots.put(startKnot.time, startKnot);
		knots.put(laterKnot.time, laterKnot);
		
		createSpline();
	}
	
	public void insertKnotOnBeat(double time) {
		double phase = Math.floor(getPhaseFromTime(time) + 0.5);
		Knot newKnot = new Knot();
		newKnot.time = time;
		newKnot.phase = phase;
		
		Knot previousKnot = getKnotFromEntry(knots.floorEntry(time));
		Knot nextKnot = getKnotFromEntry(knots.ceilingEntry(time));
		Region region;
		if (nextKnot == null) {
			region = previousKnot.regionAfter;
		} else {
			region = nextKnot.regionBefore;
		}
		
		newKnot.regionBefore = region;
		newKnot.regionAfter = region;
		
		createSpline();
	}
	
	public void deleteKnot(Knot knot) {
		if (knots.size() <= 2) {
			// Minimum of 2 knots for correct functionality
			return;
		}
		
		Knot previousKnot = getKnotFromEntry(knots.lowerEntry(knot.time));
		Knot nextKnot = getKnotFromEntry(knots.higherEntry(knot.time));
		
		Region mergedRegion = mergeRegions(knot.regionBefore, knot.regionAfter);
		if (previousKnot != null) {
			previousKnot.regionAfter = mergedRegion;
		}
		
		if (nextKnot != null) {
			nextKnot.regionBefore = mergedRegion;
		}
		
		createSpline();
	}
	
	public void moveKnot(Knot knot, double time) {
		if (knots.floorKey(knot.time) != knots.floorKey(time)) {
			// Can't move a knot past another knot
			return;
		}
		
		knots.remove(knot.time);
		knot.time = time;
		knots.put(time, knot);
		
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
	
	public double findTimeForClosestBeat(double time) {
		double phase = getPhaseFromTime(time);
		double lowerPhase = Math.floor(phase);
		double upperPhase = lowerPhase + 1;
		double lowerTime = getTimeFromPhase(lowerPhase, time);
		double upperTime = getTimeFromPhase(upperPhase, time);
		
		return upperTime - time < time - lowerTime ? upperTime : lowerTime;
	}
	
	public double findTimeForNextBeat(double time) {
		double phase = getPhaseFromTime(time + 1e-9);
		double goalPhase = Math.floor(phase + 1);
		return getTimeFromPhase(goalPhase, time);
	}
	
	public double getPhaseFromTime(double time) {
		return spline.eval(time);
	}
	
	private double getTimeFromPhase(double phase, double guess) {
		return spline.invEval(phase, guess);
	}
	
	private void createSpline() {
		spline = new Spline(knots.size() - 1);
		int splineIndex = 0;
		
		for (Knot knot : knots.values()) {
			knot.splineIndex = splineIndex;
			splineIndex++;
		}
		
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
	
	private Region mergeRegions(Region regionBefore, Region regionAfter) {
		// TODO: Implement
		return new CubicRegion();
	}
	
	private static class Knot {
		private double time;
		private double phase;
		
		private Region regionBefore;
		private Region regionAfter;
		
		private int splineIndex;
	}
	
	private interface Region {
		
	}
	
	// TODO: Linear region
	
	private class CubicRegion implements Region {
		
	}
}
