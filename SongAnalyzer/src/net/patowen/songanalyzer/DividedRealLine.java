package net.patowen.songanalyzer;

import java.util.NavigableMap;
import java.util.TreeMap;

public class DividedRealLine<Knot, Region> {
	private NavigableMap<Double, Knot> knots;
	private NavigableMap<Double, Region> regions; // Lower boundaries
	
	public DividedRealLine(Region initialRegion) {
		knots = new TreeMap<>();
		regions = new TreeMap<>();
		
		regions.put(Double.NEGATIVE_INFINITY, initialRegion);
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
	
	public void insertKnot(double pos, Knot knot, Region earlierSplitRegion, Region laterSplitRegion) {
		if (knots.containsKey(pos)) {
			throw new IllegalArgumentException("There is already a knot here.");
		}
		
		regions.put(regions.floorKey(pos), earlierSplitRegion);
		regions.put(pos, laterSplitRegion);
		knots.put(pos, knot);
	}
	
	public void removeKnot(double knotPos, Region mergedRegion) {
		if (!knots.containsKey(knotPos)) {
			throw new IllegalArgumentException("Tried to remove a knot that doesn't exist.");
		}
		
		regions.remove(knotPos);
		regions.put(regions.floorKey(knotPos), mergedRegion);
		knots.remove(knotPos);
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
}
