package net.patowen.songanalyzer.grid;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Collections;
import java.util.List;

import net.patowen.songanalyzer.userinput.InputActionDrag;

public final class Grid {
	private int width;
	private int height;
	
	private GridParams params = new GridParams();
	
	private List<? extends GridColumn> startColumns = Collections.emptyList();
	private GridColumn centerColumn = null;
	private List<? extends GridColumn> endColumns = Collections.emptyList();
	private GridColumn spanningColumn = null;
	
	private List<? extends GridRow> startRows = Collections.emptyList();
	private GridRow centerRow = null;
	private List<? extends GridRow> endRows = Collections.emptyList();
	private GridRow spanningRow = null;
	
	public void setStartColumns(List<GridColumn> startColumns) {
		this.startColumns = startColumns;
	}
	
	public void setCenterColumn(GridColumn centerColumn) {
		this.centerColumn = centerColumn;
	}
	
	public void setEndColumns(List<GridColumn> endColumns) {
		this.endColumns = endColumns;
	}
	
	public void setSpanningColumn(GridColumn spanningColumn) {
		this.spanningColumn = spanningColumn;
	}
	
	public void setStartRows(List<? extends GridRow> startRows) {
		this.startRows = startRows;
	}
	
	public void setCenterRow(GridRow centerRow) {
		this.centerRow = centerRow;
	}
	
	public void setEndRows(List<GridRow> endRows) {
		this.endRows = endRows;
	}
	
	public void setSpanningRow(GridRow spanningRow) {
		this.spanningRow = spanningRow;
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
		if (params.outerBorderWidth > 0) {
			g.drawLine(0, 0, 0, height-1);
			g.drawLine(width-1, 0, width-1, height-1);
		}
		
		if (params.outerBorderHeight > 0) {
			g.drawLine(0, 0, width-1, 0);
			g.drawLine(0, height-1, width-1, height-1);
		}
		
		if (params.interBorderWidth > 0) {
			for (GridColumn gridColumn : startColumns) {
				int x = gridColumn.getPos() + gridColumn.getSize();
				g.drawLine(x, 0, x, height-1);
			}
			
			for (GridColumn gridColumn : endColumns) {
				int x = gridColumn.getPos() - 1;
				g.drawLine(x, 0, x, height-1);
			}
		}
		
		if (params.interBorderHeight > 0) {
			for (GridRow gridRow : startRows) {
				int y = gridRow.getPos() + gridRow.getSize();
				g.drawLine(0, y, width-1, y);
			}
			
			for (GridRow gridRow : endRows) {
				int y = gridRow.getPos() - 1;
				g.drawLine(0, y, width-1, y);
			}
		}
	}
	
	private void adjustColumns() {
		int startColumnXPos = params.outerBorderWidth;
		int endColumnXPos = width - params.outerBorderWidth;
		
		if (spanningColumn != null) {
			spanningColumn.setSlot(GridSlot.spanning);
			spanningColumn.setPos(startColumnXPos);
			spanningColumn.setSize(endColumnXPos - startColumnXPos);
		}
		
		for (GridColumn gridColumn : startColumns) {
			gridColumn.setSlot(GridSlot.start);
			gridColumn.setInterBorderSize(params.interBorderWidth);
			gridColumn.setPos(startColumnXPos);
			startColumnXPos += gridColumn.getSize() + gridColumn.getInterBorderSize();
		}
		
		for (GridColumn gridColumn : endColumns) {
			gridColumn.setSlot(GridSlot.end);
			gridColumn.setInterBorderSize(params.interBorderWidth);
			endColumnXPos -= gridColumn.getSize() + gridColumn.getInterBorderSize();
			gridColumn.setPos(endColumnXPos);
		}
		
		if (centerColumn != null) {
			centerColumn.setSlot(GridSlot.center);
			centerColumn.setPos(startColumnXPos);
			centerColumn.setSize(endColumnXPos - startColumnXPos);
		}
	}
	
	private void adjustRows() {
		int startRowYPos = params.outerBorderHeight;
		int endRowYPos = height - params.outerBorderHeight;
		
		if (spanningRow != null) {
			spanningRow.setSlot(GridSlot.spanning);
			spanningRow.setPos(startRowYPos);
			spanningRow.setSize(endRowYPos - startRowYPos);
		}
		
		for (GridRow gridRow : startRows) {
			gridRow.setSlot(GridSlot.start);
			gridRow.setInterBorderSize(params.interBorderHeight);
			gridRow.setPos(startRowYPos);
			startRowYPos += gridRow.getSize() + gridRow.getInterBorderSize();
		}
		
		for (GridRow gridRow : endRows) {
			gridRow.setSlot(GridSlot.end);
			gridRow.setInterBorderSize(params.interBorderHeight);
			endRowYPos -= gridRow.getSize() + gridRow.getInterBorderSize();
			gridRow.setPos(endRowYPos);
		}
		
		if (centerRow != null) {
			centerRow.setSlot(GridSlot.center);
			centerRow.setPos(startRowYPos);
			centerRow.setSize(endRowYPos - startRowYPos);
		}
	}
	
	private GridElement getElementToResize(Point pointerCoords) {
		GridElementToResize currentBest = new GridElementToResize(pointerCoords, null, Integer.MAX_VALUE);
		for (GridColumn gridElement : startColumns) {
			currentBest.setIfBetter(gridElement);
		}
		for (GridColumn gridElement : endColumns) {
			currentBest.setIfBetter(gridElement);
		}
		for (GridRow gridElement : startRows) {
			currentBest.setIfBetter(gridElement);
		}
		for (GridRow gridElement : endRows) {
			currentBest.setIfBetter(gridElement);
		}
		return currentBest.gridElement;
	}
	
	private final class GridElementToResize {
		public Point pointerCoords;
		public GridElement gridElement;
		public int distance;
		
		public GridElementToResize(Point pointerCoords, GridElement gridElement, int distance) {
			this.pointerCoords = pointerCoords;
			this.gridElement = gridElement;
			this.distance = distance;
		}
		
		public void setIfBetter(GridElement newElement) {
			if (newElement == null) {
				return;
			}
			
			int newDistance = newElement.getResizeDistance(pointerCoords);
			if (newDistance < distance) {
				gridElement = newElement;
				distance = newDistance;
			}
		}
	}
	
	private final class ActionResize implements InputActionDrag {
		private GridElement gridElement; // TODO: Use ResizingGridPart
		private int initialHeight;
		
		@Override
		public boolean onAction(Point pos, double value) {
			gridElement = getElementToResize(pos);
			if (gridElement == null) {
				return false;
			}
			initialHeight = gridElement.getSize();
			return true;
		}
		
		@Override
		public void onDrag(Point startRelative) {
			gridElement.setSize(initialHeight + gridElement.getPointerPos(startRelative));
		}
		
		@Override
		public void onCancel() {
			gridElement.setSize(initialHeight);
		}
		
		@Override
		public void onEnd(Point startRelative) {}
	}
}
