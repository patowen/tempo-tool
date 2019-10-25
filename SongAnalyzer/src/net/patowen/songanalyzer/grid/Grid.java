package net.patowen.songanalyzer.grid;

import java.util.List;

public class Grid {
	private int width;
	private int height;
	
	private List<GridColumn> startColumns;
	private GridColumn centerColumn;
	private List<GridColumn> endColumns;
	
	private List<GridRow> startRows;
	private GridRow centerRow;
	private List<GridRow> endRows;
	
	private int outerBorderWidth;
	private int interBorderWidth;
	private int outerBorderHeight;
	private int interBorderHeight;
	
	// Actual views are stored in grid cells. The order in the list is the order they are rendered.
	// TODO: Don't store as a field. Let them be passed in during rendering and mouse events.
	private List<GridCell> gridCells;
	
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
		for (GridColumn gridColumn : startColumns) {
			gridColumn.setXPos(startColumnXPos);
			startColumnXPos += gridColumn.getWidth() + interBorderWidth;
		}
		
		int endColumnXPos = width - outerBorderWidth;
		for (GridColumn gridColumn : endColumns) {
			endColumnXPos -= gridColumn.getWidth() + interBorderWidth;
			gridColumn.setXPos(endColumnXPos);
		}
		
		centerColumn.setXPos(startColumnXPos);
		centerColumn.setWidth(endColumnXPos - startColumnXPos);
	}
	
	private void adjustRows() {
		int startRowYPos = outerBorderHeight;
		for (GridRow gridRow : startRows) {
			gridRow.setYPos(startRowYPos);
			startRowYPos += gridRow.getHeight() + interBorderHeight;
		}
		
		int endRowYPos = height - outerBorderHeight;
		for (GridRow gridRow : endRows) {
			endRowYPos -= gridRow.getHeight() + interBorderHeight;
			gridRow.setYPos(endRowYPos);
		}
		
		centerRow.setYPos(startRowYPos);
		centerRow.setHeight(endRowYPos - startRowYPos);
	}
}
