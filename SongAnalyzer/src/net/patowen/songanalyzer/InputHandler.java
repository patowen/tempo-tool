package net.patowen.songanalyzer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements MouseListener, MouseMotionListener, MouseWheelListener {
	private GuiNode rootNode;
	private GuiNode mouseNode;
	private int dragStartX, dragStartY;
	
	@Override
	public void mousePressed(MouseEvent e) {
		mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
		dragStartX = e.getX();
		dragStartY = e.getY();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseNode = rootNode.getMouseNode(e.getX(), e.getY());
	}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
}
