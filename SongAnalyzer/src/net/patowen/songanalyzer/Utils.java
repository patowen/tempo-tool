package net.patowen.songanalyzer;

import java.awt.Point;

public final class Utils {
	public static Point getRelativePoint(Point origin, Point point) {
		if (point == null) {
			return null;
		}
		return new Point(point.x - origin.x, point.y - origin.y);
	}
}
