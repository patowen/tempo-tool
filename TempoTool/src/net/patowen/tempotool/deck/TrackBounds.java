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

package net.patowen.tempotool.deck;

public class TrackBounds {
	private double width;
	private double totalSeconds;
	private double secondsStart;
	
	public TrackBounds(double secondsStart, double totalSeconds) {
		this.secondsStart = secondsStart;
		this.totalSeconds = totalSeconds;
	}
	
	public double secondsToSubpixel(double seconds) {
		return (seconds - secondsStart) * (width / totalSeconds);
	}
	
	public int secondsToPixel(double seconds) {
		return (int)Math.floor(secondsToSubpixel(seconds));
	}
	
	public double subpixelToSeconds(double subPixel) {
		return subPixel * (totalSeconds / width) + secondsStart;
	}
	
	public double pixelToSeconds(int pixel) {
		return subpixelToSeconds(pixel + 0.5);
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void zoom(double seconds, double factor) {
		secondsStart = (secondsStart - seconds) * factor + seconds;
		totalSeconds *= factor;
	}
	
	public void shiftSecondsToSubpixel(double seconds, double subpixel) {
		double oldSeconds = subpixelToSeconds(subpixel);
		secondsStart += seconds - oldSeconds;
	}
	
	public void setBounds(double secondsStart, double totalSeconds) {
		this.secondsStart = secondsStart;
		this.totalSeconds = totalSeconds;
	}
}
