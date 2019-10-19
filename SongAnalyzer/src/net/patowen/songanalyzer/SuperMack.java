package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.patowen.songanalyzer.exception.FileFormatException;
import net.patowen.songanalyzer.exception.IllegalMackTypeException;
import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeMouse;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightFree;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public class SuperMack extends View implements DimWidthControlled, DimHeightFree {
	private Mack mack;
	
	private int interBorderHeight = 1, interBorderSelectionRange = 3;
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	private InputDictionary inputDictionary;
	
	private SuperMack(Mack mack) {
		this.mack = mack;
		this.mack.setXPos(trackTabWidth + trackTabBorderWidth);
		this.mack.setYPos(0);
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new SuperMackInput.ActionResize(this), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	private static Mack createMack(int type, TrackBounds trackBounds, UserActionList userActionList) {
		switch (type) {
		case MackSeek.type:
			return new MackSeek(trackBounds);
		case MackMarker.type:
			return new MackMarker(trackBounds, userActionList);
		default:
			throw new IllegalMackTypeException();
		}
	}
	
	public static SuperMack create(int type, Integer height, TrackBounds trackBounds, UserActionList userActionList) {
		SuperMack superMack = new SuperMack(createMack(type, trackBounds, userActionList));
		superMack.trySetHeight(height == null ? superMack.mack.getDefaultHeight() : height);
		return superMack;
	}
	
	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawLine(trackTabWidth, 0, trackTabWidth, height);
		g.drawLine(0, height, width, height);
		
		mack.forwardRender(g);
	}
	
	public int getHeight() {
		return height;
	}
	
	public void trySetHeight(int height) {
		int minimumHeight = mack.getMinimumHeight();
		if (height < minimumHeight) {
			height = minimumHeight;
		}
		this.height = height;
		mack.setHeight(height);
	}
	
	public void setWidth(int width) {
		this.width = width;
		mack.setWidth(width - trackTabWidth - trackTabBorderWidth);
	}
	
	public boolean isDragHandle(Point pos) {
		return pos != null
				&& pos.x >= 0
				&& pos.x < trackTabWidth
				&& pos.y >= height - interBorderSelectionRange
				&& pos.y < height + interBorderHeight + interBorderSelectionRange;
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction appliedInput = inputDictionary.applyInput(inputType, mousePos, value);
		if (appliedInput != null) {
			return appliedInput;
		}

		if (inputType.isMouseBased()
				&& mousePos.x >= trackTabWidth + trackTabBorderWidth
				&& mousePos.x < width
				&& mousePos.y >= 0
				&& mousePos.y < height) {
			return mack.forwardInput(inputType, mousePos, value);
		}
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		if (isDragHandle(mousePos)) {
			return new MouseHoverFeedback(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		return mack.forwardMouseHover(mousePos);
	}
	
	public void save(DataOutputStream stream) throws IOException {
		stream.writeInt(height);
		stream.writeInt(mack.getType());
		mack.save(stream);
	}
	
	public static SuperMack load(DataInputStream stream, TrackBounds trackBounds, UserActionList userActionList) throws IOException, FileFormatException {
		int height = stream.readInt();
		int type = stream.readInt();
		try {
			SuperMack superMack = create(type, height, trackBounds, userActionList);
			superMack.mack.load(stream);
			return superMack;
		} catch (IllegalMackTypeException e) {
			throw new FileFormatException("Unknown mack type " + type);
		}
	}
}