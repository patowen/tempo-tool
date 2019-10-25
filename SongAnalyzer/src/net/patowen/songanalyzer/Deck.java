package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.data.Arr;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.data.Obj;
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
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(RootBundle rootBundle) {
		bundle = new DeckBundle(rootBundle);
		superMacks = new ArrayList<>();
		
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
	}
	
	@Override
	public void render(Graphics2D g) {
		setSuperMackPositions();
		bundle.trackBounds.setWidth(width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth);
		
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width-1, height-1);
		
		for (SuperMack superMack : superMacks) {
			superMack.forwardRender(g);
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
		bundle.trackBounds.setBounds(0, 60);
		
		superMacks.add(SuperMack.create(MackSeek.type, null, bundle));
		superMacks.add(SuperMack.create(MackMarker.type, null, bundle));
	}
	
	private interface Keys {
		int macks = 0;
	}
	
	public Dict save() {
		Dict dict = new Dict();
		Arr arr = new Arr();
		for (SuperMack superMack : superMacks) {
			arr.add(superMack.save());
		}
		dict.set(Keys.macks, arr);
		return dict;
	}
	
	public void load(Dict dict) throws FileFormatException {
		superMacks.clear();
		bundle.trackBounds.setBounds(0, 60);
		
		Arr arr = dict.get(Keys.macks).asArr();
		for (Obj obj : arr.get()) {
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
}
