package net.patowen.songanalyzer.grid;

import java.awt.Point;

public abstract class GridElement {
	private int pos;
	private int size;
	private int minimumSize = 0;
	private boolean resizable = false;
	private GridSlot slot;
	private int interBorderSize;
	private int resizeRange;
	
	void setPos(int pos) {
		this.pos = pos;
	}
	
	public int getPos() {
		return pos;
	}
	
	public void trySetSize(int size) {
		this.size = size;
		if (this.size < minimumSize) {
			this.size = minimumSize;
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public void setMinimumSize(int minimumSize) {
		this.minimumSize = minimumSize;
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
	
	void setResizeRange(int resizeRange) {
		this.resizeRange = resizeRange;
	}
	
	int getResizeRange() {
		return resizeRange;
	}
	
	int getResizeDistance(Point pointerCoords) {
		if (!resizable) {
			return Integer.MAX_VALUE;
		}
		
		int resizeDistance = getResizeDistanceInternal(pointerCoords);
		if (resizeDistance > resizeRange) {
			return Integer.MAX_VALUE;
		}
		return resizeDistance;
	}
	
	private int getResizeDistanceInternal(Point pointerCoords) {
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
