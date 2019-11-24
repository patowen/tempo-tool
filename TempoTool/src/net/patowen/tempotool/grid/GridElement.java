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

package net.patowen.tempotool.grid;

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
