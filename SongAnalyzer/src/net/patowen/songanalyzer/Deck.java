package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
	
	private ArrayList<SuperMack> superMacks;
	private final DeckGrid deckGrid;
	private final List<DeckRow> deckRows;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(RootBundle rootBundle) {
		bundle = new DeckBundle(rootBundle);
		superMacks = new ArrayList<>();
		
		deckRows = new ArrayList<>();
		deckGrid = new DeckGrid(deckRows);
		
		fallbackInputDictionary = new InputDictionary();
		fallbackInputDictionary.addInputMapping(new InputMapping(new Zoom(), new InputTypeScroll(false, false, false), 1));
		fallbackInputDictionary.constructDictionary();
	}
	
	private void setSuperMackPositions() {
		int yPos = outerBorderHeight;
		for (SuperMack superMack : superMacks) {
			superMack.setXPos(outerBorderWidth);
			superMack.setYPos(yPos);
			superMack.setWidth(width - outerBorderWidth * 2);
			
			yPos += superMack.getHeight() + interBorderHeight;
		}
		
		deckGrid.setWidth(width);
		deckGrid.setHeight(height);
		deckGrid.resizeMacks();
	}
	
	@Override
	public void render(Graphics2D g) {
		setSuperMackPositions();
		bundle.trackBounds.setWidth(width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth);
		
		g.setColor(Color.WHITE);
		deckGrid.renderGridlines(g);
		g.drawRect(0, 0, width-1, height-1);
		
		for (SuperMack superMack : superMacks) {
			superMack.forwardRender(g);
		}
		
		for (DeckRow deckRow : deckRows) {
			deckRow.mack.forwardRender(g);
		}
		
		g.setColor(Color.GREEN);
		
		if (bundle.audioPlayer.hasAudioStream()) {
			int pos = bundle.trackBounds.secondsToPixel(bundle.audioPlayer.getPos()) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
			g.drawLine(pos, 0, pos, height);
		}
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		if (inputType.isMouseBased()) {
			if (mousePos.x >= 0 && mousePos.y >= 0 && mousePos.x < width && mousePos.y < height) {
				for (SuperMack superMack : superMacks) {
					InputAction inputAction = superMack.forwardInput(inputType, mousePos, value);
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
		superMacks.clear();
		deckRows.clear();
		bundle.trackBounds.setBounds(0, 60);
		
		superMacks.add(SuperMack.create(MackSeek.type, null, bundle));
		superMacks.add(SuperMack.create(MackMarker.type, null, bundle));
		
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
		for (SuperMack superMack : superMacks) {
			arr.add(superMack.save());
		}
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
		superMacks.clear();
		bundle.trackBounds.setBounds(0, 60);
		
		Arr arr = dict.get(Keys.macks).asArr();
		for (Obj obj : arr.get()) {
			Dict mackDict = obj.asDict();
			try {
				DeckRow deckRow = new DeckRow(mackDict.get(MackKeys.type).asInt(), bundle);
				deckRow.gridRow.setHeight(mackDict.get(MackKeys.height).asInt());
				deckRow.mack.load(mackDict);
				deckRows.add(deckRow);
			} catch (IllegalMackTypeException e) {
				throw new FileFormatException("Unknown mack type " + e.getType());
			}
			
			superMacks.add(SuperMack.load(obj.asDict(), bundle));
		}
		
		this.setWidth(width);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		for (SuperMack superMack : superMacks) {
			MouseHoverFeedback mouseHoverFeedback = superMack.forwardMouseHover(mousePos);
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
	
	private static class DeckGrid extends Grid {
		GridColumn tabColumn = new GridColumn();
		GridColumn trackColumn = new GridColumn();
		final List<DeckRow> deckRows = new ArrayList<DeckRow>();
		
		DeckGrid(List<DeckRow> deckRows) {
			tabColumn.setWidth(8);
		}
		
		@Override
		public Iterable<GridColumn> getStartColumns() {
			return Collections.singletonList(tabColumn);
		}
		
		@Override
		public GridColumn getCenterColumn() {
			return trackColumn;
		}
		
		@Override
		public Iterable<GridRow> getStartRows() {
			return deckRows.stream().map(x -> x.gridRow).collect(Collectors.toList());
		}
		
		public void resizeMacks() {
			for (DeckRow deckRow : deckRows) {
				deckRow.mack.setXPos(trackColumn.getXPos());
				deckRow.mack.setYPos(deckRow.gridRow.getYPos());
				deckRow.mack.setWidth(trackColumn.getWidth());
				deckRow.mack.setHeight(deckRow.gridRow.getHeight());
			}
		}
	}
	
	private static class DeckRow {
		GridRow gridRow = new GridRow();
		Mack mack;
		
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
			
			gridRow.setHeight(mack.getDefaultHeight());
		}
	}
}
