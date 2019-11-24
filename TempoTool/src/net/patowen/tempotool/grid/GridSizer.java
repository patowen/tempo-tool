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

import net.patowen.tempotool.view.Sizer;

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
