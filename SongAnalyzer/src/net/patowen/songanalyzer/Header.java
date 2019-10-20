package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;

import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightFree;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public class Header extends View implements DimWidthControlled, DimHeightFree {
	private AudioFileSelector audioFileSelector;
	
	public Header(RootBundle bundle) {
		height = 32;
		
		audioFileSelector = new AudioFileSelector(bundle);
		audioFileSelector.setHeight(height);
	}

	@Override
	public void render(Graphics2D g) {
		audioFileSelector.forwardRender(g);
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return audioFileSelector.forwardInput(inputType, mousePos, value);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return audioFileSelector.applyMouseHover(mousePos);
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}
}
