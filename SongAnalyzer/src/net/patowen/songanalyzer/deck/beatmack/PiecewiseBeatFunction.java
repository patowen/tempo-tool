package net.patowen.songanalyzer.deck.beatmack;

import java.util.ArrayList;
import java.util.List;

public class PiecewiseBeatFunction implements BeatFunction {
	private List<Double> domain = new ArrayList<>(); //Size n+1
	private List<BeatFunction> regions = new ArrayList<>(); //Size n
}

/*
 * Constant tempo section operations:
 * Shift phase (drag beats together left or right)
 * Fix a point on-beat and continuously change tempo (drag left and right to resize)
 * Fix a point on-beat and set tempo to a rational value (to take advantage of integer tempo)
 * Fix an off-beat point iff there are no on-beat points in the section
 * 
 * How operations propagate:
 * Changed tempo or phase shift propagates to continuous neighbors but are absorbed by tempo ramps
 * 
 * 
 * Use cases of tempo variance:
 * Uneven tempo due to imperfection
 * Sudden tempo changes or tempo ramps for effect
 */