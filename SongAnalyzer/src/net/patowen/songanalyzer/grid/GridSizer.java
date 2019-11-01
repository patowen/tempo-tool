package net.patowen.songanalyzer.grid;

import net.patowen.songanalyzer.view.Sizer;

public class GridSizer implements Sizer {
	private final GridColumn gridColumn;
	private final GridRow gridRow;
	
	public GridSizer(GridColumn gridColumn, GridRow gridRow) {
		this.gridColumn = gridColumn;
		this.gridRow = gridRow;
	}
	
	@Override
	public int getXPos() {
		return gridColumn.getPos();
	}
	
	@Override
	public int getYPos() {
		return gridRow.getPos();
	}
	
	@Override
	public int getWidth() {
		return gridColumn.getSize();
	}
	
	@Override
	public int getHeight() {
		return gridRow.getSize();
	}
}
