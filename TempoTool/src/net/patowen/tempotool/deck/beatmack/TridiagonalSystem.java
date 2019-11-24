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
