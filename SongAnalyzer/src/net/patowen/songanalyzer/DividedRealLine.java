package net.patowen.songanalyzer;

import java.util.ArrayList;
import java.util.List;
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
	
	public List<Double> getKnotPositionList() {
		return new ArrayList<>(knots.keySet());
	}
	
	public List<Knot> getKnotList() {
		return new ArrayList<>(knots.values());
	}
	
	public List<Region> getRegionList() {
		return new ArrayList<>(regions.values());
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
