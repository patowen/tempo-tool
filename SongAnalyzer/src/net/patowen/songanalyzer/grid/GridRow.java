package net.patowen.songanalyzer.grid;

import java.awt.Point;

public class GridRow extends GridElement {
	@Override
	int getPointerPos(Point pointerCoords) {
		return pointerCoords.y;
	}
}
