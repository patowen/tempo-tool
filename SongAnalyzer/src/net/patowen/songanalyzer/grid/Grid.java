package net.patowen.songanalyzer.grid;

import java.util.Collections;

public class Grid {
	private int width;
	private int height;
	
	private int outerBorderWidth;
	private int interBorderWidth;
	private int outerBorderHeight;
	private int interBorderHeight;
	
	public Grid() {
		outerBorderWidth = 1;
		interBorderWidth = 1;
		outerBorderHeight = 1;
		interBorderHeight = 1;
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
