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
	
	private Point mousePos;
	
	public InputController(Component component) {
		activeInput = null;
		mousePos = component.getMousePosition();
		
		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		//mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (activeInput != null && activeInput.inputType.equals(new InputTypeMouse(e.getButton(), e.isControlDown(), e.isShiftDown(), e.isAltDown()))) {
			activeInput.inputHandler.onEnd(e.getX() - activeInput.startX, e.getY() - activeInput.startY);
		}
		activeInput = null;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		handleMouseMotion(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		handleMouseMotion(e);
	}
	
	private void handleMouseMotion(MouseEvent e) {
		if (activeInput != null) {
			activeInput.inputHandler.onDrag(e.getX() - activeInput.startX, e.getY() - activeInput.startY);
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	private class AugmentedInputHandler {
		InputType inputType;
		
		InputHandler inputHandler;
		
		int startX, startY;
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
	TODO: Marks need to be more complicated than just doubles. Consider custom data structure

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
		The tempo can change smoothly between anchors.
		A beat does not have to occur at an anchor.
	
Global inputs:
	S key - While held, seek bar inputs will be in place.
	Scroll - Defer to seek bar if not handled elsewhere
	Ctrl-Z - Undo
	Ctrl-Shift-Z or Ctrl-Y - Redo
	Ctrl-S or Ctrl-Shift-S - Save (Shift makes it force a dialog box)
	Ctrl-O - Open

 */
