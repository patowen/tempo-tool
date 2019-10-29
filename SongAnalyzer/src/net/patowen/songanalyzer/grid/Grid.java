package net.patowen.songanalyzer.grid;

import java.util.ArrayList;
import java.util.List;

public class Grid {
	private int width;
	private int height;
	
	private final List<GridColumn> startColumns;
	private GridColumn centerColumn;
	private final List<GridColumn> endColumns;
	private GridColumn spanningColumn;
	
	private final List<GridRow> startRows;
	private GridRow centerRow;
	private final List<GridRow> endRows;
	private GridRow spanningRow;
	
	private int outerBorderWidth;
	private int interBorderWidth;
	private int outerBorderHeight;
	private int interBorderHeight;
	
	public Grid() {
		startColumns = new ArrayList<>();
		endColumns = new ArrayList<>();
		startRows = new ArrayList<>();
		endRows = new ArrayList<>();
		
		outerBorderWidth = 1;
		interBorderWidth = 1;
		outerBorderHeight = 1;
		interBorderHeight = 1;
	}
	
	public GridColumn addStartColumn(int width) {
		GridColumn gridColumn = new GridColumn();
		gridColumn.setWidth(width);
		startColumns.add(gridColumn);
		return gridColumn;
	}
	
	public GridColumn addCenterColumn() {
		centerColumn = new GridColumn();
		return centerColumn;
	}
	
	public GridColumn addEndColumn(int width) {
		GridColumn gridColumn = new GridColumn();
		gridColumn.setWidth(width);
		endColumns.add(gridColumn);
		return gridColumn;
	}
	
	public GridColumn addSpanningColumn() {
		spanningColumn = new GridColumn();
		return spanningColumn;
	}
	
	public GridRow addStartRow(int height) {
		GridRow gridRow = new GridRow();
		gridRow.setHeight(height);
		startRows.add(gridRow);
		return gridRow;
	}
	
	public GridRow addCenterRow() {
		centerRow = new GridRow();
		return centerRow;
	}
	
	public GridRow addEndRow() {
		GridRow gridRow = new GridRow();
		gridRow.setHeight(height);
		endRows.add(gridRow);
		return gridRow;
	}
	
	public GridRow addSpanningRow() {
		spanningRow = new GridRow();
		return spanningRow;
	}
	
	public void setWidth(int width) {
		this.width = width;
		
		adjustColumns();
	}
	
	public void setHeight(int height) {
		this.height = height;
		
		adjustRows();
	}
	
	private void adjustColumns() {
		int startColumnXPos = outerBorderWidth;
		int endColumnXPos = width - outerBorderWidth;
		
		if (spanningColumn != null) {
			spanningColumn.setXPos(startColumnXPos);
			spanningColumn.setWidth(endColumnXPos - startColumnXPos);
		}
		
		for (GridColumn gridColumn : startColumns) {
			gridColumn.setXPos(startColumnXPos);
			startColumnXPos += gridColumn.getWidth() + interBorderWidth;
		}
		
		for (GridColumn gridColumn : endColumns) {
			endColumnXPos -= gridColumn.getWidth() + interBorderWidth;
			gridColumn.setXPos(endColumnXPos);
		}
		
		if (centerColumn != null) {
			centerColumn.setXPos(startColumnXPos);
			centerColumn.setWidth(endColumnXPos - startColumnXPos);
		}
	}
	
	private void adjustRows() {
		int startRowYPos = outerBorderHeight;
		int endRowYPos = height - outerBorderHeight;
		
		if (spanningRow != null) {
			spanningRow.setYPos(startRowYPos);
			spanningRow.setHeight(endRowYPos - startRowYPos);
		}
		
		for (GridRow gridRow : startRows) {
			gridRow.setYPos(startRowYPos);
			startRowYPos += gridRow.getHeight() + interBorderHeight;
		}
		
		for (GridRow gridRow : endRows) {
			endRowYPos -= gridRow.getHeight() + interBorderHeight;
			gridRow.setYPos(endRowYPos);
		}
		
		if (centerRow != null) {
			centerRow.setYPos(startRowYPos);
			centerRow.setHeight(endRowYPos - startRowYPos);
		}
	}
}
