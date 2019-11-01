package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.data.Arr;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.data.Obj;
import net.patowen.songanalyzer.exception.IllegalMackTypeException;
import net.patowen.songanalyzer.grid.Grid;
import net.patowen.songanalyzer.grid.GridColumn;
import net.patowen.songanalyzer.grid.GridRow;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeScroll;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

// The deck is main area of the application, a stack of macks (track layers) with a play bar.
public class Deck extends View implements DimWidthControlled, DimHeightControlled {
	private InputDictionary fallbackInputDictionary;
	
	private final DeckBundle bundle;
	
	private final Grid grid = new Grid();
	
	private final GridColumn tabColumn = new GridColumn();
	private final GridColumn trackColumn = new GridColumn();
	private final List<DeckRow> deckRows = new ArrayList<>();
	
	public Deck(RootBundle rootBundle) {
		bundle = new DeckBundle(rootBundle);
		
		tabColumn.setSize(8);
		
		grid.setStartColumns(Collections.singletonList(tabColumn));
		grid.setCenterColumn(trackColumn);
		grid.setStartRows(deckRows);
		
		fallbackInputDictionary = new InputDictionary();
		fallbackInputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		fallbackInputDictionary.constructDictionary();
	}
	
	private void setSuperMackPositions() {
		grid.setWidth(width);
		grid.setHeight(height);
		
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.setXPos(trackColumn.getPos());
			deckRow.mack.setYPos(deckRow.getPos());
			deckRow.mack.setWidth(trackColumn.getSize());
			deckRow.mack.setHeight(deckRow.getSize());
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		setSuperMackPositions();
		bundle.trackBounds.setWidth(trackColumn.getSize());
		
		g.setColor(Color.WHITE);
		grid.renderGridlines(g);
		
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.forwardRender(g);
		}
		
		g.setColor(Color.GREEN);
		
		if (bundle.audioPlayer.hasAudioStream()) {
			int pos = bundle.trackBounds.secondsToPixel(bundle.audioPlayer.getPos()) + trackColumn.getPos();
			g.drawLine(pos, 0, pos, height);
		}
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction gridInputAction = grid.applyInputAction(inputType, mousePos, value);
		if (gridInputAction != null) {
			return gridInputAction;
		}
		
		if (inputType.isMouseBased()) {
			if (mousePos.x >= 0 && mousePos.y >= 0 && mousePos.x < width && mousePos.y < height) {
				for (DeckRow deckRow : deckRows) {
					InputAction inputAction = deckRow.mack.forwardInput(inputType, mousePos, value);
					if (inputAction != null) {
						return inputAction;
					}
				}
			}
		}
		
		// TODO Keyboard controls on active mack
		
		return fallbackInputDictionary.applyInput(inputType, mousePos, value);
	}
	
	public void reset() {
		deckRows.clear();
		bundle.trackBounds.setBounds(0, 60);
		
		deckRows.add(new DeckRow(MackSeek.type, bundle));
		deckRows.add(new DeckRow(MackMarker.type, bundle));
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
			mackDict.set(MackKeys.height, height);
			mackDict.set(MackKeys.type, deckRow.mack.getType());
			deckRow.mack.save(mackDict);
		}
		dict.set(Keys.macks, arr);
		return dict;
	}
	
	public void load(Dict dict) throws FileFormatException {
		deckRows.clear();
		bundle.trackBounds.setBounds(0, 60);
		
		Arr arr = dict.get(Keys.macks).asArr();
		for (Obj obj : arr.get()) {
			Dict mackDict = obj.asDict();
			try {
				DeckRow deckRow = new DeckRow(mackDict.get(MackKeys.type).asInt(), bundle);
				deckRow.setSize(mackDict.get(MackKeys.height).asInt());
				deckRow.mack.load(mackDict);
				deckRows.add(deckRow);
			} catch (IllegalMackTypeException e) {
				throw new FileFormatException("Unknown mack type " + e.getType());
			}
		}
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
				bundle.trackBounds.zoom(bundle.trackBounds.pixelToSeconds(pos.x), zoomFactor);
				return true;
			}
			return false;
		}
	}
	
	private static class DeckRow extends GridRow {
		final Mack mack;
		
		DeckRow(int type, DeckBundle bundle) {
			switch (type) {
			case MackSeek.type:
				mack = new MackSeek(bundle);
				break;
			case MackMarker.type:
				mack = new MackMarker(bundle);
				break;
			default:
				throw new IllegalMackTypeException(type);
			}
			
			setSize(mack.getDefaultHeight());
		}
	}
}
