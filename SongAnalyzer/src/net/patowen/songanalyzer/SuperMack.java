package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class SuperMack extends View implements DimWidthControlled, DimHeightFree {
	private Mack mack;
	
	private int interBorderHeight = 1, interBorderSelectionRange = 3;
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	private InputDictionary inputDictionary;
	
	public SuperMack(Mack mack) {
		this.mack = mack;
		this.mack.setXPos(trackTabWidth + trackTabBorderWidth);
		this.mack.setYPos(0);
		this.height = mack.getDefaultHeight();
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new SuperMackInput.ActionResize(this), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
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
		this.height = height;
		int minimumHeight = mack.getMinimumHeight();
		if (height < minimumHeight) {
			height = minimumHeight;
		}
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
			mack.forwardInput(inputType, mousePos, value);
		}
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		if (isDragHandle(mousePos)) {
			return new MouseHoverFeedback(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		return mack.applyMouseHover(null);
	}
}