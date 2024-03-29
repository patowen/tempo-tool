/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.patowen.tempotool.view;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

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
		
		Shape prevClip = g.getClip();
		g.clipRect(0, 0, width, height);
		render(g);
		g.setClip(prevClip);
		
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
