package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class SuperMack implements View, DimWidthControlled, DimHeightFree {
	private Mack mack;
	private int height;
	
	private int width;
	
	private int interBorderHeight = 1, interBorderSelectionRange = 3;
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	private InputDictionary inputDictionary;
	
	public SuperMack(Mack mack) {
		this.mack = mack;
		this.height = mack.getDefaultHeight();
		
		inputDictionary.addInputMapping(new InputMapping(new SuperMackInput.ActionResize(this), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	public void render(Graphics2D g) {
		// TODO: Put SuperMack rendering code here and add a transformation
		// TODO: Consider altering the SuperMack to have more overlap with the deck in the outer boundary
		mack.render(g);
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
		updateMackSize();
	}
	
	public void setWidth(int width) {
		this.width = width;
		updateMackSize();
	}
	
	private void updateMackSize() {
		mack.setWidth(width - trackTabWidth - trackTabBorderWidth);
		mack.setHeight(height);
	}
	
	public boolean isDragHandle(Point pos) {
		return pos.x >= 0
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
			mack.applyInputAction(inputType, new Point(mousePos.x - trackTabWidth - trackTabBorderWidth, mousePos.y), value);
		}
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		// TODO Auto-generated method stub
		return null;
	}
}