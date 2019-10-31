package net.patowen.songanalyzer.grid;

import java.awt.Point;

public abstract class GridElement {
	private int pos;
	private int size;
	private boolean resizable;
	private GridSlot slot;
	private int interBorderSize;
	
	void setPos(int pos) {
		this.pos = pos;
	}
	
	public int getPos() {
		return pos;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}
	
	boolean isResizable() {
		return resizable;
	}
	
	void setSlot(GridSlot slot) {
		this.slot = slot;
	}
	
	public GridSlot getSlot() {
		return slot;
	}
	
	void setInterBorderSize(int interBorderSize) {
		this.interBorderSize = interBorderSize;
	}
	
	int getInterBorderSize() {
		return interBorderSize;
	}
	
	int getResizeDistance(Point pointerCoords) {
		int pointerPos = getPointerPos(pointerCoords);
		
		switch (getSlot()) {
		case start:
			return Math.abs(pointerPos * 2 + 1 - (pos + size) * 2 - getInterBorderSize());
		case end:
			return Math.abs(pointerPos * 2 + 1 - pos * 2 + getInterBorderSize());
		default:
			return Integer.MAX_VALUE;
		}
	}
	
	abstract int getPointerPos(Point pointerCoords);
}
