package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

public abstract class View {
	protected int width, height;
	private int xPos, yPos;
	
	public abstract void render(Graphics2D g);
	
	public abstract InputAction applyInputAction(InputType inputType, Point mousePos, double value);
	
	public abstract MouseHoverFeedback applyMouseHover(Point mousePos);
	
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}
	
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}
	
	public void forwardRender(Graphics2D g) {
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
}
