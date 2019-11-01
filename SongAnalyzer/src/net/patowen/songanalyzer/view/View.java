package net.patowen.songanalyzer.view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public abstract class View {
	private Sizer sizer;
	
	protected int width, height;
	private int xPos, yPos;
	
	public abstract void render(Graphics2D g);
	
	public abstract InputAction applyInputAction(InputType inputType, Point mousePos, double value);
	
	public abstract MouseHoverFeedback applyMouseHover(Point mousePos);
	
	public final void setSizer(Sizer sizer) {
		this.sizer = sizer;
	}
	
	public void forwardRender(Graphics2D g) {
		xPos = sizer.getXPos();
		yPos = sizer.getYPos();
		width = sizer.getWidth();
		height = sizer.getHeight();
		
		AffineTransform transform = g.getTransform();
		g.translate(xPos, yPos);
		render(g);
		g.setTransform(transform);
	}
	
	public InputAction forwardInput(InputType inputType, Point mousePos, double value) {
		if (mousePos == null) {
			return applyInputAction(inputType, null, value);
		} else {
			return applyInputAction(inputType, new Point(mousePos.x - xPos, mousePos.y - yPos), value);
		}
	}
	
	public MouseHoverFeedback forwardMouseHover(Point mousePos) {
		if (mousePos == null) {
			return applyMouseHover(null);
		} else {
			return applyMouseHover(new Point(mousePos.x - xPos, mousePos.y - yPos));
		}
	}
	
	public boolean isWithinView(Point pos) {
		return pos != null
				&& pos.x >= 0
				&& pos.y >= 0
				&& pos.x < width
				&& pos.y < height;
	}
}
