package net.patowen.tempotool.deck.beatmack;

public class TridiagonalSystem {
	public final int size;
	
	public final double[] a;
	public final double[] b;
	public final double[] c;
	public final double[] d;
	
	private final double[] c2;
	private final double[] d2;
	
	public final double[] x;
	
	public TridiagonalSystem(int size) {
		this.size = size;
		
		a = new double[size];
		b = new double[size];
		c = new double[size];
		d = new double[size];
		
		c2 = new double[size];
		d2 = new double[size];
		
		x = new double[size];
	}
	
	public void solve() {
		if (size == 0) {
			return; // Nothing to do
		}
		
		// Forward sweep (Note: c2[n-1] will be set but unused)
		c2[0] = c[0] / b[0];
		d2[0] = d[0] / b[0];
		
		for (int i=1; i<size; i++) {
			c2[i] = c[i] / (b[i] - a[i]*c2[i-1]);
			d2[i] = (d[i] - a[i]*d2[i-1]) / (b[i] - a[i]*c2[i-1]);
		}
		
		// Back substitution
		x[size-1] = d2[size-1];
		for (int i=size-2; i>=0; i--) {
			x[i] = d2[i] - c2[i]*x[i+1];
		}
	}
}
