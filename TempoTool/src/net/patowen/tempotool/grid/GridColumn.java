package net.patowen.tempotool.grid;

import java.awt.Point;

public class GridColumn extends GridElement {
	@Override
	int getPointerPos(Point pointerCoords) {
		return pointerCoords.x;
	}
}
