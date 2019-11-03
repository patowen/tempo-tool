package net.patowen.songanalyzer.deck.beatmack;

import java.util.Arrays;

// Ostensibly immutable; recreated when knots move
// Use linear extrapolation at endpoints
public class Spline {
	private final int numRegions;
	
	public final double[] x;
	public final double[] y;
	
	public final double[] a;
	public final double[] b;
	
	private final TridiagonalSystem system;
	
	public Spline(int numRegions) {
		this.numRegions = numRegions;
		
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
}
