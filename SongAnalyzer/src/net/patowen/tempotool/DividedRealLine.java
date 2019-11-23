package net.patowen.tempotool;

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class DividedRealLine<Knot, Region> {
	private final NavigableMap<Double, Knot> knots;
	private final NavigableMap<Double, Region> regions; // Lower boundaries
	
	public DividedRealLine(Region initialRegion) {
		knots = new TreeMap<>();
		regions = new TreeMap<>();
		
		regions.put(Double.NEGATIVE_INFINITY, initialRegion);
	}
	
	public int numKnots() {
		return knots.size();
	}
	
	public int numRegions() {
		return regions.size();
	}
	
	public Region getRegion(double pos) {
		return regions.floorEntry(pos).getValue();
	}
	
	public RegionBoundaries getRegionBoundaries(double pos) {
		return new RegionBoundaries(knots.floorKey(pos), knots.higherKey(pos));
	}
	
	public KnotNeighborhood<Region> getKnotNeighborhood(double knotPos) {
		if (!knots.containsKey(knotPos)) {
			throw new IllegalArgumentException("Tried to remove a knot that doesn't exist.");
		}
		
		return new KnotNeighborhood<Region>(regions.lowerEntry(knotPos).getValue(), regions.get(knotPos));
	}
	
	public boolean isKnot(double pos) {
		return knots.containsKey(pos);
	}
	
	public boolean isFirstKnot(double pos) {
		return knots.firstKey() == pos;
	}
	
	public boolean isLastKnot(double pos) {
		return knots.lastKey() == pos;
	}
	
	public Knot getKnot(double knotPos) {
		return knots.get(knotPos);
	}
	
	public Collection<Knot> getLaterKnots(double pos) {
		return knots.tailMap(pos, false).values();
	}
	
	public void insertKnot(double pos, Knot knot, Region earlierSplitRegion, Region laterSplitRegion) {
		if (knots.containsKey(pos)) {
			throw new IllegalArgumentException("There is already a knot here.");
		}
		
		regions.put(regions.floorKey(pos), earlierSplitRegion);
		regions.put(pos, laterSplitRegion);
		knots.put(pos, knot);
	}
	
	public void insertKnot(InsertionRemoval<Knot, Region> insertionRemoval) {
		insertKnot(insertionRemoval.pos, insertionRemoval.knot, insertionRemoval.earlierSplitRegion, insertionRemoval.laterSplitRegion);
	}
	
	public void removeKnot(double knotPos, Region mergedRegion) {
		if (!knots.containsKey(knotPos)) {
			throw new IllegalArgumentException("Tried to remove a knot that doesn't exist.");
		}
		
		regions.remove(knotPos);
		regions.put(regions.floorKey(knotPos), mergedRegion);
		knots.remove(knotPos);
	}
	
	public void removeKnot(InsertionRemoval<Knot, Region> insertionRemoval) {
		removeKnot(insertionRemoval.pos, insertionRemoval.mergedRegion);
	}
	
	public boolean canMoveKnot(double knotPos, double newPos) {
		if (!knots.containsKey(knotPos)) {
			throw new IllegalArgumentException("Cannot move a knot that doesn't exist.");
		}
		
		Double lowerKnotPos = knots.lowerKey(knotPos);
		Double higherKnotPos = knots.higherKey(knotPos);
		
		return (lowerKnotPos == null || lowerKnotPos < newPos) && (higherKnotPos == null || higherKnotPos > newPos);
	}
	
	public void moveKnot(double knotPos, double newPos) {
		if (!knots.containsKey(knotPos)) {
			throw new IllegalArgumentException("Cannot move a knot that doesn't exist.");
		}
		
		if (!canMoveKnot(knotPos, newPos)) {
			throw new IllegalArgumentException("Tried to move a knot past another knot.");
		}
		
		Knot knot = knots.remove(knotPos);
		knots.put(newPos, knot);
		
		Region region = regions.remove(knotPos);
		regions.put(newPos, region);
	}
	
	public void setKnotsAndRegions(List<Double> knotPositions, List<Knot> knots, List<Region> regions) {
		if (knotPositions.size() != knots.size() || knotPositions.size() != regions.size() - 1) {
			throw new IllegalArgumentException("Wrong number of items in one of the passed-in lists");
		}
		
		this.knots.clear();
		this.regions.clear();
		
		this.regions.put(Double.NEGATIVE_INFINITY, regions.get(0));
		for (int i=0; i<knotPositions.size(); i++) {
			this.knots.put(knotPositions.get(i), knots.get(i));
			this.regions.put(knotPositions.get(i), regions.get(i+1));
		}
	}
	
	public Collection<Double> getKnotPositions() {
		return knots.keySet();
	}
	
	public Collection<Knot> getKnots() {
		return knots.values();
	}
	
	public Collection<Region> getRegions() {
		return regions.values();
	}
	
	public InsertionRemoval<Knot, Region> prepareInsertion(double pos, Knot knot, Region earlierSplitRegion, Region laterSplitRegion) {
		return new InsertionRemoval<Knot, Region>(
				pos,
				knot,
				regions.floorEntry(pos).getValue(),
				earlierSplitRegion,
				laterSplitRegion);
	}
	
	public InsertionRemoval<Knot, Region> prepareRemoval(double knotPos, Region mergedRegion) {
		return new InsertionRemoval<Knot, Region>(
				knotPos,
				knots.get(knotPos),
				mergedRegion,
				regions.lowerEntry(knotPos).getValue(),
				regions.get(knotPos));
	}
	
	public static final class RegionBoundaries {
		public final Double startKnot;
		public final Double endKnot;
		
		private RegionBoundaries(Double startKnot, Double endKnot) {
			this.startKnot = startKnot;
			this.endKnot = endKnot;
		}
	}
	
	public static final class KnotNeighborhood<Region> {
		public final Region earlierRegion;
		public final Region laterRegion;
		
		private KnotNeighborhood(Region earlierRegion, Region laterRegion) {
			this.earlierRegion = earlierRegion;
			this.laterRegion = laterRegion;
		}
	}
	
	public static final class InsertionRemoval<Knot, Region> {
		public final double pos;
		public final Knot knot;
		public final Region mergedRegion;
		public final Region earlierSplitRegion;
		public final Region laterSplitRegion;
		
		public InsertionRemoval(double pos, Knot knot, Region mergedRegion, Region earlierSplitRegion, Region laterSplitRegion) {
			this.pos = pos;
			this.knot = knot;
			this.mergedRegion = mergedRegion;
			this.earlierSplitRegion = earlierSplitRegion;
			this.laterSplitRegion = laterSplitRegion;
		}
	}
}
