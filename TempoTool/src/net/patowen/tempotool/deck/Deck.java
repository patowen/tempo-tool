package net.patowen.tempotool.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.patowen.tempotool.AudioPlayer;
import net.patowen.tempotool.bundle.DeckBundle;
import net.patowen.tempotool.bundle.RootBundle;
import net.patowen.tempotool.data.Arr;
import net.patowen.tempotool.data.Dict;
import net.patowen.tempotool.data.FileFormatException;
import net.patowen.tempotool.data.Obj;
import net.patowen.tempotool.deck.beatmack.BeatMack;
import net.patowen.tempotool.exception.IllegalMackTypeException;
import net.patowen.tempotool.grid.Grid;
import net.patowen.tempotool.grid.GridColumn;
import net.patowen.tempotool.grid.GridRow;
import net.patowen.tempotool.grid.GridSizer;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionStandard;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeScroll;
import net.patowen.tempotool.userinput.MouseHoverFeedback;
import net.patowen.tempotool.view.View;

// The deck is main area of the application, a stack of macks (track layers) with a play bar.
public class Deck extends View {
	private InputDictionary fallbackInputDictionary;
	
	private final DeckBundle bundle;
	private final AudioPlayer audioPlayer;
	private final TrackBounds trackBounds;
	private final MackRefs mackRefs;
	
	private final Grid grid = new Grid();
	
	private final GridColumn tabColumn = new GridColumn();
	private final GridColumn trackColumn = new GridColumn();
	private final List<DeckRow> deckRows = new ArrayList<>();
	
	public Deck(RootBundle rootBundle) {
		bundle = new DeckBundle(rootBundle);
		audioPlayer = bundle.audioPlayer;
		trackBounds = bundle.trackBounds;
		mackRefs = bundle.mackRefs;
		
		tabColumn.trySetSize(8);
		
		grid.setStartColumns(Collections.singletonList(tabColumn));
		grid.setCenterColumn(trackColumn);
		grid.setStartRows(deckRows);
		
		fallbackInputDictionary = new InputDictionary();
		fallbackInputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		fallbackInputDictionary.constructDictionary();
	}
	
	@Override
	public void render(Graphics2D g) {
		if (audioPlayer.isPlaying()) {
			double maxTime = trackBounds.subpixelToSeconds(width * 0.8);
			double time = audioPlayer.getPos();
			if (time > maxTime) {
				trackBounds.shiftSecondsToSubpixel(time, width * 0.2);
			}
		}
		
		grid.setWidth(width);
		grid.setHeight(height);
		
		trackBounds.setWidth(trackColumn.getSize());
		
		g.setColor(Color.WHITE);
		grid.renderGridlines(g);
		
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.forwardRender(g);
			deckRow.mackTab.forwardRender(g);
		}
		
		g.setColor(Color.GREEN);
		
		if (audioPlayer.hasAudioStream()) {
			int pos = trackBounds.secondsToPixel(audioPlayer.getPos()) + trackColumn.getPos();
			g.drawLine(pos, 0, pos, height);
		}
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction gridInputAction = grid.applyInputAction(inputType, mousePos, value);
		if (gridInputAction != null) {
			return gridInputAction;
		}
		
		if (inputType.isMouseBased() && !(mousePos.x >= 0 && mousePos.y >= 0 && mousePos.x < width && mousePos.y < height)) {
			return null;
		}
		
		for (DeckRow deckRow : deckRows) {
			InputAction inputAction = deckRow.mack.forwardInput(inputType, mousePos, value);
			if (inputAction != null) {
				return inputAction;
			}
			
			inputAction = deckRow.mackTab.forwardInput(inputType, mousePos, value);
			if (inputAction != null) {
				return inputAction;
			}
		}
		
		return fallbackInputDictionary.applyInput(inputType, mousePos, value);
	}
	
	public void reset() {
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.destroy();
		}
		
		deckRows.clear();
		trackBounds.setBounds(0, 60);
		
		deckRows.add(new DeckRow(trackColumn, tabColumn, SeekMack.type, bundle));
		deckRows.add(new DeckRow(trackColumn, tabColumn, MarkerMack.type, bundle));
		deckRows.add(new DeckRow(trackColumn, tabColumn, BeatMack.type, bundle));
	}
	
	public void beatsaberExport(FileWriter writer) throws IOException {
		BeatMack beatMack = mackRefs.mainBeatMack;
		if (beatMack == null) {
			writer.write("Main beat mack not set. No data.");
			return;
		}
		
		beatMack.beatsaberExport(writer, audioPlayer.getLength());
	}
	
	private interface Keys {
		int macks = 0;
	}
	
	private interface MackKeys {
		int height = 128;
		int type = 129;
	}
	
	public Dict save() {
		Dict dict = new Dict();
		Arr arr = new Arr();
		for (DeckRow deckRow : deckRows) {
			Dict mackDict = new Dict();
			mackDict.set(MackKeys.height, deckRow.getSize());
			mackDict.set(MackKeys.type, deckRow.mack.getType());
			deckRow.mack.save(mackDict);
			arr.add(mackDict);
		}
		dict.set(Keys.macks, arr);
		return dict;
	}
	
	public void load(Dict dict) throws FileFormatException {
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.destroy();
		}
		
		deckRows.clear();
		trackBounds.setBounds(0, 60);
		
		Arr arr = dict.get(Keys.macks).asArr();
		for (Obj obj : arr.get()) {
			Dict mackDict = obj.asDict();
			try {
				DeckRow deckRow = new DeckRow(trackColumn, tabColumn, mackDict.get(MackKeys.type).asInt(), bundle);
				deckRow.trySetSize(mackDict.get(MackKeys.height).asInt());
				deckRow.mack.load(mackDict);
				deckRows.add(deckRow);
			} catch (IllegalMackTypeException e) {
				throw new FileFormatException("Unknown mack type " + e.getType());
			}
		}
		
		grid.setWidth(width);
		grid.setHeight(height);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		MouseHoverFeedback gridFeedback = grid.applyMouseHover(mousePos);
		if (gridFeedback != null) {
			return gridFeedback;
		}
		
		for (DeckRow deckRow : deckRows) {
			MouseHoverFeedback mouseHoverFeedback = deckRow.mack.forwardMouseHover(mousePos);
			if (mouseHoverFeedback != null) {
				return mouseHoverFeedback;
			}
		}
		return null;
	}
	
	private class Zoom implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (isWithinView(pos)) {
				double zoomFactor = Math.exp(value * 0.1);
				trackBounds.zoom(trackBounds.pixelToSeconds(pos.x), zoomFactor);
				return true;
			}
			return false;
		}
	}
	
	private static class DeckRow extends GridRow {
		final Mack mack;
		final MackTab mackTab;
		
		DeckRow(GridColumn trackColumn, GridColumn tabColumn, int type, DeckBundle bundle) {
			MackRefs mackRefs = bundle.mackRefs;
			
			switch (type) {
			case SeekMack.type:
				mack = new SeekMack(bundle);
				break;
			case MarkerMack.type:
				MarkerMack markerMack = new MarkerMack(bundle);
				mackRefs.metronomeMarkerMack = markerMack;
				mack = markerMack;
				break;
			case BeatMack.type:
				BeatMack beatMack = new BeatMack(bundle);
				mackRefs.mainBeatMack = beatMack;
				mack = beatMack;
				break;
			default:
				throw new IllegalMackTypeException(type);
			}
			
			mack.setSizer(new GridSizer(trackColumn, this));
			trySetSize(mack.getDefaultHeight());
			setMinimumSize(mack.getMinimumHeight());
			
			mackTab = new MackTab(bundle, mack);
			mackTab.setSizer(new GridSizer(tabColumn, this));
			
			setResizable(true);
		}
	}
}
