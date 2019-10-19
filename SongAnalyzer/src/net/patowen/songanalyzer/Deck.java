package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.patowen.songanalyzer.exception.FileFormatException;
import net.patowen.songanalyzer.undo.UserActionList;
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
	
	private UserActionList userActionList;
	
	private ArrayList<SuperMack> superMacks;
	private TrackBounds trackBounds;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	public Deck(UserActionList userActionList) {
		this.userActionList = userActionList;
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
		
		trackBounds.setWidth(width - outerBorderWidth * 2 - trackTabWidth - trackTabBorderWidth);
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
		
		int pos = trackBounds.secondsToPixel(0) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
		g.drawLine(pos, 0, pos, height);
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
		trackBounds = new TrackBounds(0, 10);
		
		superMacks.add(SuperMack.create(MackSeek.type, null, trackBounds, this.userActionList));
		superMacks.add(SuperMack.create(MackMarker.type, null, trackBounds, this.userActionList));
	}
	
	public void save(DataOutputStream stream) throws IOException {
		stream.writeInt(superMacks.size());
		for (SuperMack superMack : superMacks) {
			superMack.save(stream);
		}
	}
	
	public void load(DataInputStream stream) throws IOException, FileFormatException {
		superMacks.clear();
		trackBounds = new TrackBounds(0, 10);
		
		int numSuperMacks = stream.readInt();
		for (int i=0; i<numSuperMacks; i++) {
			superMacks.add(SuperMack.load(stream, trackBounds, userActionList));
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
