package net.patowen.songanalyzer.grid;

import java.awt.Graphics2D;
import java.util.Collections;

public class Grid {
	private int width;
	private int height;
	
	private final int outerBorderWidth;
	private final int outerBorderHeight;
	private final int interBorderWidth;
	private final int interBorderHeight;
	
	public Grid(int outerBorderWidth, int outerBorderHeight, int interBorderWidth, int interBorderHeight) {
		this.outerBorderWidth = outerBorderWidth;
		this.outerBorderHeight = outerBorderHeight;
		this.interBorderWidth = interBorderWidth;
		this.interBorderHeight = interBorderHeight;
	}
	
	public Iterable<GridColumn> getStartColumns() {
		return Collections.emptyList();
	}
	
	public GridColumn getCenterColumn() {
		return null;
	}
	
	public Iterable<GridColumn> getEndColumns() {
		return Collections.emptyList();
	}
	
	public GridColumn getSpanningColumn() {
		return null;
	}
	
	public Iterable<GridRow> getStartRows() {
		return Collections.emptyList();
	}
	
	public GridRow getCenterRow() {
		return null;
	}
	
	public Iterable<GridRow> getEndRows() {
		return Collections.emptyList();
	}
	
	public GridRow getSpanningRow() {
		return null;
	}
	
	public final void setWidth(int width) {
		this.width = width;
		
		adjustColumns();
	}
	
	public final void setHeight(int height) {
		this.height = height;
		
		adjustRows();
	}
	
	public final void renderGridlines(Graphics2D g) {
		// TODO: Handle border sizes other than 0 or 1
		if (outerBorderWidth > 0) {
			g.drawLine(0, 0, 0, height-1);
			g.drawLine(width-1, 0, width-1, height-1);
		}
		
		if (outerBorderHeight > 0) {
			g.drawLine(0, 0, width-1, 0);
			g.drawLine(0, height-1, width-1, height-1);
		}
		
		if (interBorderWidth > 0) {
			for (GridColumn gridColumn : getStartColumns()) {
				int x = gridColumn.getXPos() + gridColumn.getWidth();
				g.drawLine(x, 0, x, height-1);
			}
			
			for (GridColumn gridColumn : getEndColumns()) {
				int x = gridColumn.getXPos() - 1;
				g.drawLine(x, 0, x, height-1);
			}
		}
		
		if (interBorderHeight > 0) {
			for (GridRow gridRow : getStartRows()) {
				int y = gridRow.getYPos() + gridRow.getHeight();
				g.drawLine(0, y, width-1, y);
			}
			
			for (GridRow gridRow : getEndRows()) {
				int y = gridRow.getYPos() - 1;
				g.drawLine(0, y, width-1, y);
			}
		}
	}
	
	private void adjustColumns() {
		int startColumnXPos = outerBorderWidth;
		int endColumnXPos = width - outerBorderWidth;
		
		GridColumn spanningColumn = getSpanningColumn();
		if (spanningColumn != null) {
			spanningColumn.setXPos(startColumnXPos);
			spanningColumn.setWidth(endColumnXPos - startColumnXPos);
		}
		
		for (GridColumn gridColumn : getStartColumns()) {
			gridColumn.setXPos(startColumnXPos);
			startColumnXPos += gridColumn.getWidth() + interBorderWidth;
		}
		
		for (GridColumn gridColumn : getEndColumns()) {
			endColumnXPos -= gridColumn.getWidth() + interBorderWidth;
			gridColumn.setXPos(endColumnXPos);
		}
		
		GridColumn centerColumn = getCenterColumn();
		if (centerColumn != null) {
			centerColumn.setXPos(startColumnXPos);
			centerColumn.setWidth(endColumnXPos - startColumnXPos);
		}
	}
	
	private void adjustRows() {
		int startRowYPos = outerBorderHeight;
		int endRowYPos = height - outerBorderHeight;
		
		GridRow spanningRow = getSpanningRow();
		if (spanningRow != null) {
			spanningRow.setYPos(startRowYPos);
			spanningRow.setHeight(endRowYPos - startRowYPos);
		}
		
		for (GridRow gridRow : getStartRows()) {
			gridRow.setYPos(startRowYPos);
			startRowYPos += gridRow.getHeight() + interBorderHeight;
		}
		
		for (GridRow gridRow : getEndRows()) {
			endRowYPos -= gridRow.getHeight() + interBorderHeight;
			gridRow.setYPos(endRowYPos);
		}
		
		GridRow centerRow = getCenterRow();
		if (centerRow != null) {
			centerRow.setYPos(startRowYPos);
			centerRow.setHeight(endRowYPos - startRowYPos);
		}
	}
}
