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
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputType;
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
		fallbackInputDictionary.constructDictionary();
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
		
		for (SuperMack superMack : superMacks) {
			superMack.setWidth(width - outerBorderWidth * 2);
		}
		
		bundle.getTrackBounds().setWidth(width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth);
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	private void setSuperMackPositions() {
		int yPos = outerBorderHeight;
		for (SuperMack superMack : superMacks) {
			superMack.setXPos(outerBorderWidth);
			superMack.setYPos(yPos);
			
			yPos += superMack.getHeight() + interBorderHeight;
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		setSuperMackPositions();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width-1, height-1);
		
		for (SuperMack superMack : superMacks) {
			superMack.forwardRender(g);
		}
		
		g.setColor(Color.GREEN);
		
		if (bundle.getAudioPlayer().hasAudioStream()) {
			int pos = bundle.getTrackBounds().secondsToPixel(bundle.getAudioPlayer().getPos()) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
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
		bundle.getTrackBounds().setBounds(0, 10);
		
		superMacks.add(SuperMack.create(MackSeek.type, null, bundle));
		superMacks.add(SuperMack.create(MackMarker.type, null, bundle));
	}
	
	private interface Keys {
		byte macks = 0;
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
		bundle.getTrackBounds().setBounds(0, 10);
		
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
}
