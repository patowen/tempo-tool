package net.patowen.songanalyzer.grid;

import java.util.List;

public class Grid {
	private int width;
	private int height;
	
	private List<GridRow> startRows;
	private List<GridRow> centerRow;
	private List<GridRow> endRows;
	
	private List<GridColumn> startColumn;
	private List<GridColumn> centerColumn;
	private List<GridColumn> endColumn;
	
	private int outerBorderWidth;
	private int outerBorderHeight;
	private int interBorderWidth;
	private int interBorderHeight;
	
	// Actual views are stored in grid cells. The order in the list is the order they are rendered.
	private List<GridCell> gridCells;
	
	public void setWidth(int width) {
		this.width = width;
		
		// TODO: Adjust grid columns
	}
	
	public void setHeight(int width) {
		this.width = width;
		
		// TODO: Adjust grid rows
	}
}
