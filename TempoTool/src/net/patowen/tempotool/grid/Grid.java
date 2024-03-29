/*
   Copyright 2019 Patrick Owen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.patowen.tempotool.grid;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputActionDrag;
import net.patowen.tempotool.userinput.InputDictionary;
import net.patowen.tempotool.userinput.InputMapping;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.InputTypeMouse;
import net.patowen.tempotool.userinput.MouseHoverFeedback;

public final class Grid {
	private int width;
	private int height;
	
	private GridParams params = new GridParams();
	
	private final InputDictionary inputDictionary = new InputDictionary();
	
	private List<? extends GridColumn> startColumns = Collections.emptyList();
	private GridColumn centerColumn = null;
	private List<? extends GridColumn> endColumns = Collections.emptyList();
	private GridColumn spanningColumn = null;
	
	private List<? extends GridRow> startRows = Collections.emptyList();
	private GridRow centerRow = null;
	private List<? extends GridRow> endRows = Collections.emptyList();
	private GridRow spanningRow = null;
	
	public Grid() {
		inputDictionary.addInputMapping(new InputMapping(new ActionResize(), new InputTypeMouse(MouseEvent.BUTTON1, false, false, false), 1));
		inputDictionary.constructDictionary();
	}
	
	public void setAsOuterGrid() {
		params.outerBorderWidth = 1;
		params.outerBorderHeight = 1;
	}
	
	public void setStartColumns(List<? extends GridColumn> startColumns) {
		this.startColumns = startColumns;
	}
	
	public void setCenterColumn(GridColumn centerColumn) {
		this.centerColumn = centerColumn;
	}
	
	public void setEndColumns(List<? extends GridColumn> endColumns) {
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
	
	public void setEndRows(List<? extends GridRow> endRows) {
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
	
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return inputDictionary.applyInput(inputType, mousePos, value);
	}
	
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		GridElement gridElement = getElementToResize(mousePos);
		
		if (gridElement == null) {
			return null;
		}
		
		if (gridElement instanceof GridColumn) {
			return new MouseHoverFeedback(new Cursor(Cursor.E_RESIZE_CURSOR));
		}
		
		if (gridElement instanceof GridRow) {
			return new MouseHoverFeedback(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		
		return null;
	}
	
	// TODO: Adjust grid element positions if preferred center or spanning row/column size cannot be satisfied
	private void adjustColumns() {
		int startColumnXPos = params.outerBorderWidth;
		int endColumnXPos = width - params.outerBorderWidth;
		
		if (spanningColumn != null) {
			spanningColumn.setSlot(GridSlot.spanning);
			spanningColumn.setPos(startColumnXPos);
			spanningColumn.trySetSize(endColumnXPos - startColumnXPos);
		}
		
		for (GridColumn gridColumn : startColumns) {
			gridColumn.setSlot(GridSlot.start);
			gridColumn.setInterBorderSize(params.interBorderWidth);
			gridColumn.setResizeRange(params.resizeXRange);
			gridColumn.setPos(startColumnXPos);
			startColumnXPos += gridColumn.getSize() + gridColumn.getInterBorderSize();
		}
		
		for (GridColumn gridColumn : endColumns) {
			gridColumn.setSlot(GridSlot.end);
			gridColumn.setInterBorderSize(params.interBorderWidth);
			gridColumn.setResizeRange(params.resizeXRange);
			endColumnXPos -= gridColumn.getSize() + gridColumn.getInterBorderSize();
			gridColumn.setPos(endColumnXPos);
		}
		
		if (centerColumn != null) {
			centerColumn.setSlot(GridSlot.center);
			centerColumn.setPos(startColumnXPos);
			centerColumn.trySetSize(endColumnXPos - startColumnXPos);
		}
	}
	
	private void adjustRows() {
		int startRowYPos = params.outerBorderHeight;
		int endRowYPos = height - params.outerBorderHeight;
		
		if (spanningRow != null) {
			spanningRow.setSlot(GridSlot.spanning);
			spanningRow.setPos(startRowYPos);
			spanningRow.trySetSize(endRowYPos - startRowYPos);
		}
		
		for (GridRow gridRow : startRows) {
			gridRow.setSlot(GridSlot.start);
			gridRow.setInterBorderSize(params.interBorderHeight);
			gridRow.setResizeRange(params.resizeYRange);
			gridRow.setPos(startRowYPos);
			startRowYPos += gridRow.getSize() + gridRow.getInterBorderSize();
		}
		
		for (GridRow gridRow : endRows) {
			gridRow.setSlot(GridSlot.end);
			gridRow.setInterBorderSize(params.interBorderHeight);
			gridRow.setResizeRange(params.resizeYRange);
			endRowYPos -= gridRow.getSize() + gridRow.getInterBorderSize();
			gridRow.setPos(endRowYPos);
		}
		
		if (centerRow != null) {
			centerRow.setSlot(GridSlot.center);
			centerRow.setPos(startRowYPos);
			centerRow.trySetSize(endRowYPos - startRowYPos);
		}
	}
	
	private GridElement getElementToResize(Point pointerCoords) {
		if (pointerCoords == null) {
			return null;
		}
		
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
		private GridElement gridElement;
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
			gridElement.trySetSize(initialHeight + gridElement.getPointerPos(startRelative));
		}
		
		@Override
		public void onCancel() {
			gridElement.trySetSize(initialHeight);
		}
		
		@Override
		public void onEnd(Point startRelative) {}
	}
}
