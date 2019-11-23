package net.patowen.songanalyzer.deck.beatmack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// A new Spline needs to be constructed when the number of knots changes.
public class Spline {
	private final int numRegions;
	
	public final double[] x;
	public final double[] y;
	
	public final double[] a;
	public final double[] b;
	
	private final Section[] sections;
	
	public Spline(List<KnotType> knots) {
		if (knots.size() < 2) {
			throw new IllegalArgumentException("Spline needs at least two knots");
		}
		this.numRegions = knots.size() - 1;
		
		x = new double[numRegions + 1];
		y = new double[numRegions + 1];
		a = new double[numRegions];
		b = new double[numRegions];
		
		sections = createSections(knots);
	}
	
	private static Section[] createSections(List<KnotType> knotTypes) {
		ArrayList<Integer> sectionLengths = new ArrayList<Integer>();
		sectionLengths.add(0);
		for (KnotType knotType : knotTypes) {
			if (knotType == KnotType.NonDifferentiable) {
				sectionLengths.add(1);
			} else {
				sectionLengths.set(sectionLengths.size() - 1, sectionLengths.get(sectionLengths.size() - 1) + 1);
			}
		}
		
		sectionLengths.set(sectionLengths.size() - 1, sectionLengths.get(sectionLengths.size() - 1) - 1);
		System.out.println(sectionLengths);
		
		Section[] sections = new Section[sectionLengths.size()];
		int totalIndex = 0;
		
		for (int sectionIndex = 0; sectionIndex < sectionLengths.size(); sectionIndex++) {
			int offset = totalIndex;
			int numRegions = sectionLengths.get(sectionIndex);
			boolean[] useEarlier = new boolean[numRegions + 1];
			boolean[] useLater = new boolean[numRegions + 1];
			for (int indexInSection = 0; indexInSection <= numRegions; indexInSection++) {
				switch (knotTypes.get(totalIndex)) {
				case Smoothest:
					useEarlier[indexInSection] = true;
					useLater[indexInSection] = true;
					totalIndex++;
					break;
				case ConformToEarlier:
					useEarlier[indexInSection] = true;
					useLater[indexInSection] = false;
					totalIndex++;
					break;
				case ConformToLater:
					useEarlier[indexInSection] = false;
					useLater[indexInSection] = true;
					totalIndex++;
					break;
				case NonDifferentiable:
					useEarlier[indexInSection] = indexInSection > 0;
					useLater[indexInSection] = indexInSection < numRegions;
					break;
				}
			}
			
			sections[sectionIndex] = new Section(offset, numRegions, useEarlier, useLater);
		}
		
		return sections;
	}
	
	public void computeSpline() {
		for (Section section : sections) {
			section.computeSpline(x, y, a, b);
		}
	}
	
	public double eval(double xVal) {
		int i = Arrays.binarySearch(x, xVal);
		if (i >= 0) {
			return y[i];
		}
		i = -i - 2;
		
		return eval(xVal, i);
	}
	
	public double eval(double xVal, int region) {
		if (region == -1) {
			double t = (xVal - x[region+1]) / (x[region+2] - x[region+1]);
			return y[region+1] + t * (y[region+2] - y[region+1] + a[region+1]);
		}
		
		if (region == numRegions) {
			double s = (x[region] - xVal) / (x[region] - x[region-1]);
			return y[region] - s * (y[region] - y[region-1] - b[region-1]);
		}
		
		double t = (xVal - x[region]) / (x[region+1] - x[region]);
		double s = 1 - t;
		
		return s*y[region] + t*y[region+1] + t*s*(s*a[region] + t*b[region]);
	}
	
	public double derivative(double xVal) {
		int i = Arrays.binarySearch(x, xVal);
		if (i < 0) {
			i = -i - 2;
		}
		
		return derivative(xVal, i);
	}
	
	public double derivative(double xVal, int region) {
		if (region == -1) {
			return (y[region+2] - y[region+1] + a[region+1]) / (x[region+2] - x[region+1]);
		}
		
		if (region == numRegions) {
			return (y[region] - y[region-1] - b[region-1]) / (x[region] - x[region-1]);
		}
		
		double t = (xVal - x[region]) / (x[region+1] - x[region]);
		double s = 1 - t;
		
		return (y[region+1] - y[region] + (s - t)*(s*a[region] + t*b[region]) + t*s*(b[region] - a[region])) / (x[region+1] - x[region]);
	}
	
	public Double invEval(double y, double guess) {
		// Start in the right region
		int region = Arrays.binarySearch(this.y, y);
		if (region >= 0) {
			return x[region];
		}
		
		region = -region - 2;
		if (region >= 0 && guess < x[region]) {
			guess = x[region];
		}
		
		if (region < numRegions && guess > x[region + 1]) {
			guess = x[region + 1];
		}
		
		// Newton's method
		for (int i=0; i<10; i++) {
			double newGuess = guess - (eval(guess, region) - y) / derivative(guess, region);
			if (newGuess > guess - 1e-12 && newGuess < guess + 1e-12) {
				return newGuess;
			}
			guess = newGuess;
		}
		
		System.err.println("Newton's method failed to converge in time");
		return null;
	}
	
	public enum KnotType {
		Smoothest,
		ConformToEarlier,
		ConformToLater,
		NonDifferentiable,
	}
	
	private static class Section {
		final int offset;
		final int numRegions;
		final boolean[] useEarlier;
		final boolean[] useLater;
		
		final TridiagonalSystem system;
		
		Section(int offset, int numRegions, boolean[] useEarlier, boolean[] useLater) {
			this.offset = offset;
			this.numRegions = numRegions;
			this.useEarlier = useEarlier;
			this.useLater = useLater;
			
			this.system = new TridiagonalSystem(numRegions + 1);
		}
		
		void reset() {
			Arrays.fill(system.a, 0.0);
			Arrays.fill(system.b, 0.0);
			Arrays.fill(system.c, 0.0);
			Arrays.fill(system.d, 0.0);
		}
		
		void computeSpline(double[] x, double[] y, double[] a, double[] b) {
			reset();
			
			for (int i=0; i<numRegions; i++) {
				double coeffTerm = 1.0 / (x[offset + i + 1] - x[offset + i]);
				double constTerm = 3 * (y[offset + i + 1] - y[offset + i]) * (coeffTerm * coeffTerm);
				
				if (useLater[i]) {
					system.b[i] += 2 * coeffTerm;
					system.c[i] += coeffTerm;
					system.d[i] += constTerm;
				}
				
				if (useEarlier[i+1]) {
					system.a[i+1] += coeffTerm;
					system.b[i+1] += 2 * coeffTerm;
					system.d[i+1] += constTerm;
				}
			}
			
			system.solve();
			
			for (int i=0; i<numRegions; i++) {
				a[offset + i] = system.x[i] * (x[offset + i + 1] - x[offset + i]) - (y[offset + i + 1] - y[offset + i]);
				b[offset + i] = -system.x[i+1] * (x[offset + i + 1] - x[offset + i]) + (y[offset + i + 1] - y[offset + i]);
			}
		}
	}
}
