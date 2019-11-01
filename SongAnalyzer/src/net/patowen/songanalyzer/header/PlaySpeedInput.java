package net.patowen.songanalyzer.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.TreeMap;
import java.util.TreeSet;

import net.patowen.songanalyzer.AudioPlayer;
import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionDrag;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class PlaySpeedInput extends HeaderView {
	private final AudioPlayer audioPlayer;
	
	private TreeSet<RationalNumber> playSpeeds = new TreeSet<>();
	private TreeMap<Double, RationalNumber> angles = new TreeMap<>();
	private RationalNumber currentPlaySpeed;
	
	private InputDictionary inputDictionary;
	
	public PlaySpeedInput(RootBundle bundle) {
		audioPlayer = bundle.audioPlayer;
		
		populatePlaySpeeds(10);
		populateAngles();
		currentPlaySpeed = new RationalNumber(1, 1);
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new ChangePlaySpeed(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	private void populatePlaySpeeds(int maxNumeratorOrDenominator) {
		for (int denominator = 1; denominator <= maxNumeratorOrDenominator; denominator++) {
			for (int numerator = 1; numerator <= maxNumeratorOrDenominator; numerator++) {
				playSpeeds.add(new RationalNumber(numerator, denominator));
			}
		}
	}
	
	private void populateAngles() {
		for (RationalNumber playSpeed : playSpeeds) {
			angles.put(playSpeed.getAngle(), playSpeed);
		}
	}
	
	@Override
	public int getPreferredWidth() {
		return 50;
	}
	
	@Override
	public void render(Graphics2D g) {
		Shape prevClip = g.getClip();
		g.clipRect(0, 0, width, height);
		
		g.setColor(Color.WHITE);
		String text;
		text = currentPlaySpeed.numerator + "/" + currentPlaySpeed.denominator;
		g.drawString(text, 12, height - 12);
		
		g.setClip(prevClip);
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private static class RationalNumber implements Comparable<RationalNumber> {
		public int numerator;
		
		 // Denominator must be positive, undefined behavior if negative.
		// Zero is treated as positive if we are not restricted to real numbers.
		public int denominator;
		
		public RationalNumber(int numerator, int denominator) {
			this.numerator = numerator;
			this.denominator = denominator;
		}
		
		public double getApproximateValue() {
			return (double)numerator / (double)denominator;
		}
		
		public double getAngle() {
			return Math.atan2(numerator, denominator);
		}
		
		@Override
		public int compareTo(RationalNumber other) {
			return Integer.compare(this.numerator * other.denominator, other.numerator * this.denominator);
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof RationalNumber)) {
				return false;
			}
			RationalNumber other = (RationalNumber) o;
			
			return this.compareTo(other) == 0;
		}
	}
	
	private class ChangePlaySpeed implements InputActionDrag {
		private RationalNumber initialPlaySpeed;
		private double initialAngle;
		
		@Override
		public boolean onAction(Point pos, double value) {
			if (pos.x >= 0 && pos.y >= 0 && pos.x < width && pos.y < height) {
				initialPlaySpeed = currentPlaySpeed;
				initialAngle = initialPlaySpeed.getAngle();
				return true;
			}
			
			return false;
		}
		
		@Override
		public void onDrag(Point startRelative) {
			double testAngle = initialAngle - startRelative.y * 0.001;
			Double floorAngle = angles.floorKey(testAngle);
			Double ceilingAngle = angles.ceilingKey(testAngle);
			double newAngle;
			
			if (floorAngle == null) {
				newAngle = ceilingAngle;
			} else if (ceilingAngle == null) {
				newAngle = floorAngle;
			} else {
				newAngle = ceilingAngle - testAngle < testAngle - floorAngle ? ceilingAngle : floorAngle;
			}
			
			currentPlaySpeed = angles.get(newAngle);
		}
		
		@Override
		public void onCancel() {
			currentPlaySpeed = initialPlaySpeed;
		}
		
		@Override
		public void onEnd(Point startRelative) {
			audioPlayer.setSpeed(currentPlaySpeed.getApproximateValue());
		}
	}
}
