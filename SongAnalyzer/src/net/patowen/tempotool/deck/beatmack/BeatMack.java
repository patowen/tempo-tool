package net.patowen.tempotool.deck.beatmack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.patowen.tempotool.Ticker;
import net.patowen.tempotool.TickerSource;
import net.patowen.tempotool.DividedRealLine.InsertionRemoval;
import net.patowen.tempotool.bundle.DeckBundle;
import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.deck.Mack;
import net.patowen.tempotool.deck.MackRefs;
import net.patowen.tempotool.deck.MarkerMack;
import net.patowen.tempotool.deck.TrackBounds;
import net.patowen.tempotool.deck.beatmack.BeatFunction.Knot;
import net.patowen.tempotool.deck.beatmack.BeatFunction.Region;
import net.patowen.tempotool.undo.UserAction;
import net.patowen.tempotool.undo.UserActionList;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionDrag;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.InputTypeScroll;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

public class BeatMack extends Mack {
	public static final int type = 2;
	
	private InputDictionary inputDictionary;
	
	private final UserActionList userActionList;
	private final TrackBounds trackBounds;
	private final MackRefs mackRefs;
	private final Ticker ticker;
	private final TickerSource tickerSource;
	
	private final BeatFunction beatFunction;
	
	private double minTempo;
	private double maxTempo;
	
	private int selectionRange = 3;
	
	public BeatMack(DeckBundle bundle) {
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new MoveKnotAtMouse(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new InsertKnotAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new DeleteKnotAtMouse(), new InputTypeMouse(MouseEvent.BUTTON3, false, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new CycleRegionType(), new InputTypeMouse(MouseEvent.BUTTON3, true, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new ChangeRegionPhaseDisplacement(), new InputTypeScroll(true, false, false), -1));
		inputDictionary.addInputMapping(new InputMapping(new VerticalZoom(), new InputTypeScroll(false, true, false), 1));
		inputDictionary.constructDictionary();
		
		userActionList = bundle.userActionList;
		trackBounds = bundle.trackBounds;
		mackRefs = bundle.mackRefs;
		ticker = bundle.ticker;
		
		beatFunction = new BeatFunction();
		
		minTempo = 0;
		maxTempo = 5;
		
		tickerSource = new BeatMackTickerSource(beatFunction);
		ticker.addSource(tickerSource);
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void render(Graphics2D g) {
		//renderBeats(g);
		renderScatterplot(g);
		renderTempoGraph(g);
		//renderPhaseGraph(g);
		renderRegions(g);
		renderKnots(g);
	}
	
	@SuppressWarnings("unused")
	private void renderBeats(Graphics2D g) {
		g.setColor(Color.GRAY);
		double currentPhase = beatFunction.getPhaseFromTime(trackBounds.subpixelToSeconds(0));
		for (int i=0; i<width-1; i++) {
			double nextPhase = beatFunction.getPhaseFromTime(trackBounds.subpixelToSeconds(i+1));
			if (Math.ceil(currentPhase) < Math.ceil(nextPhase)) {
				g.drawLine(i, 0, i, height-1);
			}
			currentPhase = nextPhase;
		}
	}
	
	private void renderScatterplot(Graphics2D g) {
		g.setColor(Color.WHITE);
		MarkerMack metronomeMarkerMack = mackRefs.metronomeMarkerMack;
		if (metronomeMarkerMack == null) {
			return;
		}
		
		Double currentTime = trackBounds.subpixelToSeconds(0);
		double maxTime = trackBounds.subpixelToSeconds(width);
		
		while (currentTime < maxTime) {
			currentTime = beatFunction.findTimeForNextBeat(currentTime);
			if (currentTime == null) {
				return;
			}
			Double closest = metronomeMarkerMack.getClosestMark(currentTime);
			if (closest == null) {
				return;
			}
			double offset = closest - currentTime;
			int x = trackBounds.secondsToPixel(currentTime);
			int y = (int)Math.floor(height / 2 + offset * 1000);
			
			g.drawLine(x, y, x, y);
		}
	}
	
	private void renderKnots(Graphics2D g) {
		g.setColor(Color.CYAN);
		for (double time : beatFunction.getAllKnotTimes()) {
			int pixelX = trackBounds.secondsToPixel(time);
			g.drawLine(pixelX, height/2 - 4, pixelX, height/2 + 4);
		}
	}
	
	private void renderRegions(Graphics2D g) {
		int pixelY = height/2;
		for (int pixelX=0; pixelX<width; pixelX++) {
			double time = trackBounds.pixelToSeconds(pixelX);
			switch (beatFunction.getRegion(time).getType()) {
			case Region.cubic:
				g.setColor(new Color(0.0f, 0.5f, 0.5f));
				break;
			case Region.linear:
				g.setColor(new Color(0.5f, 0.5f, 0.0f));
				break;
			}
			
			g.drawLine(pixelX, pixelY, pixelX, pixelY);
		}
	}
	
	private void renderTempoGraph(Graphics2D g) {
		g.setColor(Color.BLUE);
		int xPrevious = 0, yPrevious = 0;
		for (int x = -1; x < width+1; x++) {
			double time = trackBounds.pixelToSeconds(x);
			double tempo = beatFunction.getTempoFromTime(time);
			int y = (int)Math.floor((tempo - maxTempo) / (minTempo - maxTempo) * height);
			if (x != -1) {
				g.drawLine(xPrevious, yPrevious, x, y);
			}
			xPrevious = x;
			yPrevious = y;
		}
	}
	
	@SuppressWarnings("unused")
	private void renderPhaseGraph(Graphics2D g) {
		g.setColor(Color.CYAN);
		double phasePrev = beatFunction.getPhaseFromTime(trackBounds.pixelToSeconds(-1));
		double phaseBase = Math.floor(phasePrev);
		for (int pixelX = 0; pixelX <= width; pixelX++) {
			double phase = beatFunction.getPhaseFromTime(trackBounds.pixelToSeconds(pixelX));
			while (true) {
				g.drawLine(pixelX - 1, phaseToPixel(phasePrev - phaseBase), pixelX, phaseToPixel(phase - phaseBase));
				if (phase - phaseBase > 1) {
					phaseBase += 1;
				} else {
					break;
				}
			}
			phasePrev = phase;
		}
	}
	
	private int phaseToPixel(double phase) {
		return height - 1 - (int)(Math.floor(phase * height));
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
	
	private interface Keys {
		int beatFunction = 0;
	}
	
	@Override
	public void save(Dict dict) {
		Dict beatFunctionDict = new Dict();
		beatFunction.save(beatFunctionDict);
		dict.set(Keys.beatFunction, beatFunctionDict);
	}
	
	@Override
	public void load(Dict dict) throws FileFormatException {
		Dict defaultBeatFunctionDict = new Dict();
		beatFunction.save(defaultBeatFunctionDict);
		Dict beatFunctionDict = dict.getOrDefault(Keys.beatFunction, defaultBeatFunctionDict).asDict();
		beatFunction.load(beatFunctionDict);
	}
	
	@Override
	public void destroy() {
		ticker.removeSource(tickerSource);
	}
	
	@Override
	public void handleAudibleChange(boolean audible) {
		if (audible) {
			ticker.addSource(tickerSource);
		} else {
			ticker.removeSource(tickerSource);
		}
	}
	
	public void beatsaberExport(FileWriter writer, double totalTime) throws IOException {
		double lastBeat = -1e-6;
		ArrayList<Double> beats = new ArrayList<Double>();
		
		while (true) {
			double currentBeat = beatFunction.findTimeForNextBeat(lastBeat);
			if (currentBeat >= totalTime) {
				break;
			}
			beats.add(currentBeat);
			lastBeat = currentBeat;
		}
		
		if (beats.size() < 2) {
			writer.write("Song too short; no beats.");
			return;
		}
		
		int avgTempoBpm = (int)Math.round(((double)(beats.size() - 1)) / (beats.get(beats.size() - 1) - beats.get(0)) * 60);
		avgTempoBpm = 75;
		writer.write("Using display tempo: " + avgTempoBpm + System.lineSeparator());
		double avgTempo = (double)avgTempoBpm / 60.0;
		double tolerance = 0.000005;
		
		writer.write("\"_BPMChanges\":[");
		int startIndex = 0;
		for (int endIndex = 1; endIndex < beats.size(); endIndex++) {
			if (endIndex + 1 == beats.size() || getDeviationFromAverageTempo(beats, startIndex, endIndex + 1) > tolerance) {
				double start = beats.get(startIndex);
				double end = beats.get(endIndex);
				double bpm = 60.0 * (double)(endIndex - startIndex) / (end - start);
				double time = start * avgTempo;
				if (startIndex != 0) {
					writer.write(",");
				}
				writer.write("{\"_BPM\":" + bpm + ",\"_time\":" + time + ",\"_beatsPerBar\":4,\"_metronomeOffset\":1}");
				
				startIndex = endIndex;
			}
		}
		writer.write("]");
	}
	
	private double getDeviationFromAverageTempo(ArrayList<Double> beats, int startIndex, int endIndex) {
		double maxDeviation = 0;
		double secondsPerBeat = (beats.get(endIndex) - beats.get(startIndex)) / (double)(endIndex - startIndex);
		double firstBeat = beats.get(startIndex);
		
		for (int i=startIndex+1; i<=endIndex-1; i++) {
			double deviation = Math.abs(beats.get(i) - (firstBeat + secondsPerBeat * (i - startIndex)));
			if (deviation > maxDeviation) {
				maxDeviation = deviation;
			}
		}
		
		return maxDeviation;
	}
	
	private class MoveKnotAtMouse implements InputActionDrag {
		private double currentTime;
		private double initialTime;
		
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double time = trackBounds.pixelToSeconds(pos.x);
				double potentialKnot = beatFunction.findClosestKnot(time);
				
				int knotPixelX = trackBounds.secondsToPixel(potentialKnot);
				
				if (pos.x >= knotPixelX - selectionRange && pos.x <= knotPixelX + selectionRange) {
					currentTime = potentialKnot;
					initialTime = potentialKnot;
					return true;
				}
			}
			return false;
		}

		@Override
		public void onDrag(Point startRelative) {
			double newTime = trackBounds.subpixelToSeconds(trackBounds.secondsToSubpixel(initialTime) + startRelative.x);
			if (beatFunction.moveKnot(currentTime, newTime)) {
				currentTime = newTime;
			}
		}

		@Override
		public void onCancel() {
			beatFunction.moveKnot(currentTime, initialTime);
		}

		@Override
		public void onEnd(Point startRelative) {
			beatFunction.moveKnot(currentTime, initialTime);
			userActionList.applyAction(new KnotMotionAction(
					initialTime,
					trackBounds.subpixelToSeconds(trackBounds.secondsToSubpixel(initialTime) + startRelative.x)));
		}
	}
	
	private class KnotMotionAction implements UserAction {
		private final double startTime;
		private final double endTime;
		
		public KnotMotionAction(double startTime, double endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		@Override
		public void exec() {
			beatFunction.moveKnot(startTime, endTime);
		}
		
		@Override
		public void undo() {
			beatFunction.moveKnot(endTime, startTime);
		}
	}
	
	private class InsertKnotAtMouse implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double time = trackBounds.pixelToSeconds(pos.x);
				Double beatTime = beatFunction.findTimeForClosestBeat(time);
				if (beatTime == null) {
					return false;
				}
				
				int beatPixelX = trackBounds.secondsToPixel(beatTime);
				
				if (pos.x >= beatPixelX - selectionRange && pos.x <= beatPixelX + selectionRange) {
					InsertionRemoval<Knot, Region> insertionRemoval = beatFunction.getKnotOnBeatToInsert(beatTime);
					if (insertionRemoval != null) {
						userActionList.applyAction(new KnotInsertionAction(insertionRemoval));
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private class KnotInsertionAction implements UserAction {
		private final InsertionRemoval<Knot, Region> insertionRemoval;
		
		public KnotInsertionAction(InsertionRemoval<Knot, Region> insertionRemoval) {
			this.insertionRemoval = insertionRemoval;
		}
		
		@Override
		public void exec() {
			beatFunction.insertKnot(insertionRemoval);
		}

		@Override
		public void undo() {
			beatFunction.deleteKnot(insertionRemoval);
		}
	}
	
	private class DeleteKnotAtMouse implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double time = trackBounds.pixelToSeconds(pos.x);
				double potentialKnot = beatFunction.findClosestKnot(time);
				
				int knotPixelX = trackBounds.secondsToPixel(potentialKnot);
				
				if (pos.x >= knotPixelX - selectionRange && pos.x <= knotPixelX + selectionRange) {
					if (beatFunction.canDeleteKnot(potentialKnot)) {
						InsertionRemoval<Knot, Region> insertionRemoval = beatFunction.getKnotToDelete(potentialKnot);
						userActionList.applyAction(new KnotDeletionAction(insertionRemoval));
						return true;
					}
				}
			}
			return false;
		}
	}
	
	private class KnotDeletionAction implements UserAction {
		private final InsertionRemoval<Knot, Region> insertionRemoval;
		
		public KnotDeletionAction(InsertionRemoval<Knot, Region> insertionRemoval) {
			this.insertionRemoval = insertionRemoval;
		}
		
		@Override
		public void exec() {
			beatFunction.deleteKnot(insertionRemoval);
		}
		
		@Override
		public void undo() {
			beatFunction.insertKnot(insertionRemoval);
		}
	}
	
	private class ChangeRegionPhaseDisplacement implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double time = trackBounds.pixelToSeconds(pos.x);
				if (!beatFunction.regionHasPhaseDisplacement(time)) {
					return false;
				}
				
				double original = beatFunction.getRegionPhaseDisplacement(time);
				double desired = Math.max(1, original + value);
				
				userActionList.applyAction(new PhaseDisplacementModificationAction(time, original, desired));
				
				return true;
			}
			return false;
		}
	}
	
	private class PhaseDisplacementModificationAction implements UserAction {
		final double time;
		final double original;
		final double desired;
		
		public PhaseDisplacementModificationAction(double time, double original, double desired) {
			this.time = time;
			this.original = original;
			this.desired = desired;
		}
		
		@Override
		public void exec() {
			beatFunction.setRegionPhaseDisplacement(time, desired);
		}
		
		@Override
		public void undo() {
			beatFunction.setRegionPhaseDisplacement(time, original);
		}
	}
	
	private class VerticalZoom implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double centerTempo = maxTempo + pos.y * (minTempo - maxTempo) / (double)height;
				double zoomFactor = Math.exp(value * 0.1);
				
				minTempo = (minTempo - centerTempo) * zoomFactor + centerTempo;
				maxTempo = (maxTempo - centerTempo) * zoomFactor + centerTempo;
				
				return true;
			}
			return false;
		}
	}
	
	private class RegionTypeCycleAction implements UserAction {
		final Region region;
		final int original;
		final int desired;
		
		public RegionTypeCycleAction(Region region, int original, int desired) {
			this.region = region;
			this.original = original;
			this.desired = desired;
		}
		
		@Override
		public void exec() {
			beatFunction.setRegionType(region, desired);
		}
		
		@Override
		public void undo() {
			beatFunction.setRegionType(region, original);
		}
	}
	
	private class CycleRegionType implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double time = trackBounds.pixelToSeconds(pos.x);
				Region region = beatFunction.getRegion(time);
				int originalRegionType = region.getType();
				int desiredRegionType = Region.cubic;
				switch (originalRegionType) {
				case Region.cubic:
					desiredRegionType = Region.linear;
					break;
				case Region.linear:
					desiredRegionType = Region.cubic;
					break;
				}
				userActionList.applyAction(new RegionTypeCycleAction(region, originalRegionType, desiredRegionType));
				return true;
			}
			return false;
		}
	}
}
