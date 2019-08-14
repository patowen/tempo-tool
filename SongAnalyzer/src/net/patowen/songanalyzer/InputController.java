package net.patowen.songanalyzer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputController implements MouseListener, MouseMotionListener, MouseWheelListener {
	private GuiNode rootNode;
	
	private AugmentedInputHandler activeInput;
	
	private Point mousePos; // Used only for mouse-hover key presses
	
	public InputController(Component component) {
		activeInput = null;
		mousePos = component.getMousePosition();
		
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		InputType inputType = new InputTypeMouse(e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
		InputHandler inputHandler = rootNode.getInputHandler(inputType, e.getX(), e.getY());
		Point relativePoint = getRelativePoint(inputHandler.parentNode.getPos(), e.getPoint());
		
		if (inputHandler instanceof InputHandler.Standard) {
			InputHandler.Standard standard = (InputHandler.Standard) inputHandler;
			standard.inputAction.onInput(relativePoint, standard.factor);
		} else if (inputHandler instanceof InputHandler.Dragging) {
			if (activeInput == null) { // TODO
				InputHandler.Dragging dragging = (InputHandler.Dragging) inputHandler;
				dragging.inputAction.onStart(relativePoint);
				activeInput = new AugmentedInputHandler(dragging, e.getPoint());
			}
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (activeInput != null) {
			InputType inputType = new InputTypeMouse(e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown());
			if (activeInput.dragging.inputType.equals(inputType)) {
				activeInput.dragging.inputAction.onEnd(getRelativePoint(e.getPoint(), activeInput.start));
			}
			activeInput = null;
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos = e.getPoint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = e.getPoint();
		if (activeInput != null) {
			activeInput.dragging.inputAction.onDrag(getRelativePoint(e.getPoint(), activeInput.start));
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mousePos = e.getPoint();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		mousePos = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	private Point getRelativePoint(Point origin, Point point) {
		return new Point(point.x - origin.x, point.y - origin.y);
	}
	
	private class AugmentedInputHandler {
		InputHandler.Dragging dragging;
		Point start;
		
		public AugmentedInputHandler(InputHandler.Dragging dragging, Point start) {
			this.dragging = dragging;
			this.start = start;
		}
	}
}

/*

Tab inputs:
	Click tab - Select mack (Clicking on standard mack space will normally select the mack as well)
	Shift-click tab - Bind mack to selected mack, or unbind (toggle)
		Seek macks contribute bound marker macks to the ticker. Marker macks are bound to seek macks by default
		Beat macks try to place beats based on bound marker macks.

Seek mack inputs:
	Left click - Move playbar to appropriate location
	Horizontal arrow keys - Horizontal moves forward and back by amounts not dependent on zoom but dependent on modifier keys
	Vertical arrow keys - Change play speed
	Right click and drag - Move view left and right
	Shift-right click and drag - Zoom in and out
	Scroll - Zoom in and out

Marker mack inputs:
	Left click - Selection with modifiers working as expected. Click and drag selection works with shift key
	Right click - Place/delete marks based on whether shift is pressed
	N - Place mark at playbar location
	Horizontal arrow keys - Move selected marks by amount dependent on zoom level and modifier keys. Action canceled if collision occurs
	Ctrl-A - Select all
	Delete - Delete selected marks
	TODO: Marks may need to be more complicated than just doubles. Consider custom data structure

Beat mack inputs:
	Left click - Select anchors (See marker mack inputs for details)
	Right click - Place/delete anchors based on whether shift is pressed based on bound marker macks.
	Horizontal arrow keys - Move selected anchors left or right by one mark
	??? - Show graph of beat interval over time
	??? - Show graph of beat rate over time
	??? - Show graph of offset of nearest mark to each beat
	??? - Zoom graph vertically
	??? - Reset vertical zoom of graph
	??? - Find best fit
	??? - Set tempo by hand
	??? - Lock/unlock tempo
	??? - Toggle force constant tempo
	
	Notes:
		Beats start based on the first mark and end based on the last mark.
		Anchors can be placed anywhere and mark a boundary of a piecewise function.
		Sometimes, at an anchor, the tempo is altered, but sometimes, even the phase is altered, starting afresh.
		The tempo can change smoothly between anchors. Any function definition is based on endpoints.
		A beat does not have to occur at an anchor.
		In finding best fit, anchors can have phase locked at 0, position locked in place, or both (adding constraints).
		Anchors can be moved forward or backwards by beat, but for simplicity, phase will be set to 0 without moving beats.
		Some operations involve selecting a region between anchors. Finding best fit is one example
		Some operations involve selecting one side of an anchor. Moving an anchor is one example (to know how much to move it)
		Best fit operations can query marks outside range, but not beats outside range
		
		Region states:
			Blank - Do not include any beats.
			Fixed to anchor - Phase at endpoints and beat count is fixed; moving anchors moves all beats
			Fixed to time - Moving anchors keeps beats in place.
			Locked - Anchors cannot be moved at all
			(Anchors can choose what to be fixed to independently)
	
Global inputs:
	S key - While held, seek bar inputs will be in place.
	Scroll - Defer to seek bar if not handled elsewhere
	Ctrl-Z - Undo
	Ctrl-Shift-Z or Ctrl-Y - Redo
	Ctrl-S or Ctrl-Shift-S - Save (Shift makes it force a dialog box)
	Ctrl-O - Open

 */
