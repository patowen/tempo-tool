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

package net.patowen.tempotool.deck.beatmack;

import java.util.ArrayList;
import java.util.List;

import net.patowen.tempotool.DividedRealLine;
import net.patowen.tempotool.DividedRealLine.KnotNeighborhood;
import net.patowen.tempotool.DividedRealLine.RegionBoundaries;
import net.patowen.tempotool.data.Arr;
import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.data.Obj;
import net.patowen.tempotool.deck.beatmack.Spline.KnotType;

public class BeatFunction {
	private Spline spline;
	private final DividedRealLine<Knot, Region> func;
	
	public BeatFunction() {
		func = new DividedRealLine<BeatFunction.Knot, BeatFunction.Region>(new Region(Region.cubic));
		
		Knot startKnot = new Knot();
		startKnot.phase = 0;
		
		Knot laterKnot = new Knot();
		laterKnot.phase = 8;
		
		func.insertKnot(0, startKnot, new Region(Region.cubic), new Region(Region.cubic));
		func.insertKnot(8, laterKnot, new Region(Region.cubic), new Region(Region.cubic));
		
		createSpline();
	}
	
	public DividedRealLine.InsertionRemoval<Knot, Region> getKnotOnBeatToInsert(double time) {
		if (func.isKnot(time)) {
			return null;
		}
		
		double phase = Math.floor(getPhaseFromTime(time) + 0.5);
		Knot newKnot = new Knot();
		newKnot.phase = phase;
		
		Region region = func.getRegion(time);
		Region[] newRegions = splitRegion(region);
		
		return func.prepareInsertion(time, newKnot, newRegions[0], newRegions[1]);
	}
	
	public void insertKnot(DividedRealLine.InsertionRemoval<Knot, Region> insertionRemoval) {
		func.insertKnot(insertionRemoval);
		createSpline();
	}
	
	public boolean canDeleteKnot(double time) {
		return func.numKnots() > 2;
	}
	
	public boolean regionHasPhaseDisplacement(double time) {
		RegionBoundaries regionBoundaries = func.getRegionBoundaries(time);
		return regionBoundaries.startKnot != null && regionBoundaries.endKnot != null;
	}
	
	public double getRegionPhaseDisplacement(double time) {
		RegionBoundaries regionBoundaries = func.getRegionBoundaries(time);
		return func.getKnot(regionBoundaries.endKnot).phase - func.getKnot(regionBoundaries.startKnot).phase;
	}
	
	public Region getRegion(double time) {
		return func.getRegion(time);
	}
	
	public void setRegionType(Region region, int type) {
		region.type = type;
		createSpline();
	}
	
	public void setRegionPhaseDisplacement(double time, double phaseDisplacement) {
		double phaseDisplacementDelta = phaseDisplacement - getRegionPhaseDisplacement(time);
		for (Knot knot : func.getLaterKnots(time)) {
			knot.phase += phaseDisplacementDelta;
		}
		
		updateSpline();
	}
	
	public DividedRealLine.InsertionRemoval<Knot, Region> getKnotToDelete(double time) {
		KnotNeighborhood<Region> knotNeighborhood = func.getKnotNeighborhood(time);
		Region mergedRegion = mergeRegions(knotNeighborhood.earlierRegion, knotNeighborhood.laterRegion);
		return func.prepareRemoval(time, mergedRegion);
	}
	
	public void deleteKnot(DividedRealLine.InsertionRemoval<Knot, Region> insertionRemoval) {
		func.removeKnot(insertionRemoval);
		createSpline();
	}
	
	public boolean moveKnot(double currentTime, double newTime) {
		if (!func.canMoveKnot(currentTime, newTime)) {
			return false;
		}
		
		func.moveKnot(currentTime, newTime);
		currentTime = newTime;
		
		updateSpline();
		return true;
	}
	
	public Double findClosestKnot(double time) {
		RegionBoundaries regionBoundaries = func.getRegionBoundaries(time);
		Double lower = regionBoundaries.startKnot;
		Double upper = regionBoundaries.endKnot;
		
		if (lower == null && upper == null) {
			return null;
		}
		
		if (lower == null) {
			return upper;
		}
		
		if (upper == null) {
			return lower;
		}
		
		return upper - time < time - lower ? upper : lower;
	}
	
	public Iterable<Double> getAllKnotTimes() {
		return func.getKnotPositions();
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
		
		Double timeForNextBeat = getTimeFromPhase(goalPhase, time);
		if (timeForNextBeat == null || timeForNextBeat <= time) {
			return null;
		}
		
		return timeForNextBeat;
	}
	
	public boolean hasDiscontinuousTempo(double time) {
		if (!func.isKnot(time) || func.isFirstKnot(time) || func.isLastKnot(time)) {
			return false;
		}
		KnotNeighborhood<Region> knotNeighborhood = func.getKnotNeighborhood(time);
		return knotNeighborhood.earlierRegion.type == Region.linear && knotNeighborhood.laterRegion.type == Region.linear;
	}
	
	public double getPhaseFromTime(double time) {
		return spline.eval(time);
	}
	
	public double getTempoFromTime(double time) {
		return spline.derivative(time);
	}
	
	private Double getTimeFromPhase(double phase, double guess) {
		double closestKnot = findClosestKnot(guess);
		if (func.getKnot(closestKnot).phase == phase) {
			return closestKnot;
		}
		
		return spline.invEval(phase, guess);
	}
	
	private void createSpline() {
		ArrayList<KnotType> splineKnots = new ArrayList<>();
		int splineIndex = 0;
		
		for (double time : func.getKnotPositions()) {
			Knot knot = func.getKnot(time);
			knot.splineIndex = splineIndex;
			
			KnotNeighborhood<Region> knotNeighborhood = func.getKnotNeighborhood(time);
			
			KnotType knotType;
			if (func.isFirstKnot(time)) {
				knotType = KnotType.ConformToLater;
			} else if (func.isLastKnot(time)) {
				knotType = KnotType.ConformToEarlier;
			} else if (knotNeighborhood.earlierRegion.type == Region.cubic && knotNeighborhood.laterRegion.type == Region.cubic) {
				knotType = KnotType.Smoothest;
			} else if (knotNeighborhood.earlierRegion.type == Region.cubic && knotNeighborhood.laterRegion.type == Region.linear) {
				knotType = KnotType.ConformToLater;
			} else if (knotNeighborhood.earlierRegion.type == Region.linear && knotNeighborhood.laterRegion.type == Region.cubic) {
				knotType = KnotType.ConformToEarlier;
			} else if (knotNeighborhood.earlierRegion.type == Region.linear && knotNeighborhood.laterRegion.type == Region.linear) {
				knotType = KnotType.NonDifferentiable;
			} else {
				throw new IllegalStateException("Invalid region type");
			}
			
			splineKnots.add(knotType);
			splineIndex++;
		}
		
		spline = new Spline(splineKnots);
		updateSpline();
	}
	
	private void updateSpline() {
		for (double time : func.getKnotPositions()) {
			Knot knot = func.getKnot(time);
			int i = knot.splineIndex;
			spline.x[i] = time;
			spline.y[i] = knot.phase;
		}
		
		spline.computeSpline();
	}
	
	// Splitting followed by merging regions should be a no-op, or undo/redo will be broken.
	private Region[] splitRegion(Region region) {
		return new Region[] {new Region(region.type), new Region(region.type)};
	}
	
	private Region mergeRegions(Region regionBefore, Region regionAfter) {
		if (regionBefore.type == Region.linear || regionAfter.type == Region.linear) {
			return new Region(Region.linear);
		}
		return new Region(Region.cubic);
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
		
		for (double time : func.getKnotPositions()) {
			Dict knotDict = new Dict();
			knotDict.set(KnotKeys.time, time);
			knotDict.set(KnotKeys.phase, func.getKnot(time).phase);
			knotArr.add(knotDict);
		}
		
		for (Region region : func.getRegions()) {
			Dict regionDict = new Dict();
			regionDict.set(RegionKeys.type, region.type);
			regionArr.add(regionDict);
		}
		
		dict.set(Keys.knots, knotArr);
		dict.set(Keys.regions, regionArr);
	}
	
	public void load(Dict dict) throws FileFormatException {
		List<Obj> knotArr = dict.get(Keys.knots).asArr().get();
		List<Obj> regionArr = dict.get(Keys.regions).asArr().get();
		
		if (knotArr.size() + 1 != regionArr.size()) {
			throw new FileFormatException("Knots and regions don't correspond");
		}

		List<Double> knotPositions = new ArrayList<>();
		List<Knot> knots = new ArrayList<>();
		for (int i=0; i<knotArr.size(); i++) {
			Dict knotDict = knotArr.get(i).asDict();
			double time = knotDict.get(KnotKeys.time).asDouble();
			knotPositions.add(time);

			Knot knot = new Knot();
			knot.phase = knotDict.get(KnotKeys.phase).asDouble();
			knots.add(knot);
		}
		
		List<Region> regions = new ArrayList<Region>();
		for (int i=0; i<regionArr.size(); i++) {
			Dict regionDict = regionArr.get(i).asDict();
			regions.add(new Region(regionDict.get(RegionKeys.type).asInt()));
		}
		
		func.setKnotsAndRegions(knotPositions, knots, regions);
		
		createSpline();
	}
	
	public static class Knot {
		private double phase;
		
		private int splineIndex;
	}
	
	public class Region {
		public static final int cubic = 0;
		public static final int linear = 1;
		
		private int type;
		
		public Region(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
}
