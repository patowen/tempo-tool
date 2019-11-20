package net.patowen.songanalyzer.deck;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.View;

public class MackTab extends View {
	private boolean selected;
	
	public MackTab() {
		selected = false;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public void render(Graphics2D g) {
		if (selected) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
		}
	}
	
	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return null;
	}
	
	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}
}
