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

package net.patowen.tempotool.userinput;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.patowen.tempotool.view.View;

public class InputController implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	private Component component;
	
	private View rootNode;
	
	private ActiveInput activeInput;
	
	private Point mousePos; // Used only for mouse-hover key presses
	
	public InputController(Component component, View rootNode) {
		activeInput = null;
		mousePos = component.getMousePosition();
		
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
		component.addKeyListener(this);
		
		this.rootNode = rootNode;
		this.component = component;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		InputType inputType = new InputTypeMouse(e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
		mousePos = e.getPoint();
		handleAction(inputType, 1);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (activeInput != null) {
			InputType inputType = new InputTypeMouse(e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
			if (activeInput.inputType.fuzzyEquals(inputType)) {
				Point startRelative = activeInput.getRelativePoint(e.getPoint());
				activeInput.drag.onDrag(startRelative);
				activeInput.drag.onEnd(startRelative);
				component.repaint();
				activeInput = null;
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		InputType inputType = new InputTypeScroll(e.isControlDown(), e.isShiftDown(), e.isAltDown());
		mousePos = e.getPoint();
		handleAction(inputType, e.getPreciseWheelRotation());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos = e.getPoint();
		handleMouseHoverFeedback();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e.getPoint();
		if (activeInput != null) {
			activeInput.drag.onDrag(activeInput.getRelativePoint(e.getPoint()));
			component.repaint();
		}
		handleMouseHoverFeedback();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mousePos = e.getPoint();
		handleMouseHoverFeedback();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mousePos = null;
		handleMouseHoverFeedback();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		InputType inputType = new InputTypeKeyboard(e.getKeyCode(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
		handleAction(inputType, 1);
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	private void handleAction(InputType inputType, double value) {
		InputAction inputAction = rootNode.applyInputAction(inputType, mousePos, value);
		
		if (inputAction == null) {
			return;
		}
		
		if (inputAction.cancelsDrag() && activeInput != null) {
			activeInput.drag.onCancel();
		}
		
		if (inputAction instanceof InputActionDrag) {
			if (activeInput == null) {
				InputActionDrag drag = (InputActionDrag) inputAction;
				activeInput = new ActiveInput(drag, mousePos, inputType);
			}
		}
		
		component.repaint();
	}
	
	private void handleMouseHoverFeedback() {
		if (activeInput == null) {
			MouseHoverFeedback feedback = rootNode.applyMouseHover(mousePos);
			if (mousePos != null) {
				if (feedback == null) {
					this.component.setCursor(null);
				} else {
					this.component.setCursor(feedback.getCursor());
				}
			}
		}
	}
	
	private static class ActiveInput {
		InputActionDrag drag;
		Point start;
		InputType inputType;
		
		public ActiveInput(InputActionDrag drag, Point start, InputType inputType) {
			this.drag = drag;
			this.start = start;
			this.inputType = inputType;
		}
		
		public Point getRelativePoint(Point point) {
			if (point == null) {
				return null;
			}
			return new Point(point.x - start.x, point.y - start.y);
		}
	}
}
