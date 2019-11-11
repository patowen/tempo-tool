package net.patowen.songanalyzer.deck.beatmack;

import java.util.Arrays;
import java.util.List;

// A new Spline needs to be constructed when the number of knots changes.
public class Spline {
	private final int numRegions;
	
	public final double[] x;
	public final double[] y;
	
	public final double[] a;
	public final double[] b;
	
	private final TridiagonalSystem system;
	
	public Spline(List<KnotType> knots) {
		if (knots.size() < 2) {
			throw new IllegalArgumentException("Spline needs at least two knots");
		}
		this.numRegions = knots.size() - 1;
		
		x = new double[numRegions + 1];
		y = new double[numRegions + 1];
		a = new double[numRegions];
		b = new double[numRegions];
		
		system = new TridiagonalSystem(numRegions + 1);
	}
	
	public void computeSpline() {
		Arrays.fill(system.a, 0.0);
		Arrays.fill(system.b, 0.0);
		Arrays.fill(system.c, 0.0);
		Arrays.fill(system.d, 0.0);
		
		for (int i=0; i<numRegions; i++) {
			double coeffTerm = 1.0 / (x[i+1] - x[i]);
			
			system.a[i+1] += coeffTerm;
			system.b[i] += 2 * coeffTerm;
			system.b[i+1] += 2 * coeffTerm;
			system.c[i] += coeffTerm;
			
			double constTerm = 3 * (y[i+1] - y[i]) * (coeffTerm * coeffTerm);
			
			system.d[i] += constTerm;
			system.d[i+1] += constTerm;
		}
		
		system.solve();
		
		for (int i=0; i<numRegions; i++) {
			a[i] = system.x[i] * (x[i+1] - x[i]) - (y[i+1] - y[i]);
			b[i] = -system.x[i+1] * (x[i+1] - x[i]) + (y[i+1] - y[i]);
		}
	}
	
	public double eval(double xVal) {
		int i = Arrays.binarySearch(x, xVal);
		if (i >= 0) {
			return y[i];
		}
		i = -i - 2;
		
		if (i == -1) {
			double t = (xVal - x[i+1]) / (x[i+2] - x[i+1]);
			return y[i+1] + t * (y[i+2] - y[i+1] + a[i+1]);
		}
		
		if (i == numRegions) {
			double s = (x[i] - xVal) / (x[i] - x[i-1]);
			return y[i] - s * (y[i] - y[i-1] - b[i-1]);
		}
		
		double t = (xVal - x[i]) / (x[i+1] - x[i]);
		double s = 1 - t;
		
		return s*y[i] + t*y[i+1] + t*s*(s*a[i] + t*b[i]);
	}
	
	public double derivative(double xVal) {
		int i = Arrays.binarySearch(x, xVal);
		if (i < 0) {
			i = -i - 2;
		}
		
		if (i == -1) {
			return (y[i+2] - y[i+1] + a[i+1]) / (x[i+2] - x[i+1]);
		}
		
		if (i == numRegions) {
			return (y[i] - y[i-1] - b[i-1]) / (x[i] - x[i-1]);
		}
		
		double t = (xVal - x[i]) / (x[i+1] - x[i]);
		double s = 1 - t;
		
		return (y[i+1] - y[i] + (s - t)*(s*a[i] + t*b[i]) + t*s*(b[i] - a[i])) / (x[i+1] - x[i]);
	}
	
	public Double invEval(double y, double guess) {
		for (int i=0; i<10; i++) {
			double newGuess = guess - (eval(guess) - y) / derivative(guess);
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
}
