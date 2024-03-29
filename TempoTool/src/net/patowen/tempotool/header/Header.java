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

package net.patowen.tempotool.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.patowen.tempotool.bundle.RootBundle;
import net.patowen.tempotool.grid.Grid;
import net.patowen.tempotool.grid.GridRow;
import net.patowen.tempotool.userinput.InputAction;
import net.patowen.tempotool.userinput.InputType;
import net.patowen.tempotool.userinput.MouseHoverFeedback;
import net.patowen.tempotool.view.View;

public class Header extends View {
	private final RootBundle bundle;
	
	private final Grid grid;
	private final GridRow gridRow;
	private final List<HeaderColumn> headerColumns = new ArrayList<>();
	
	public Header(RootBundle bundle) {
		this.bundle = bundle;
		
		grid = new Grid();
		gridRow = new GridRow();
		grid.setCenterRow(gridRow);
		
		reset();
		grid.setStartColumns(headerColumns);
	}
	
	public void reset() {
		headerColumns.clear();
		headerColumns.add(new HeaderColumn(gridRow, new AudioFileSelector(bundle)));
		headerColumns.add(new HeaderColumn(gridRow, new PlaySpeedInput(bundle)));
	}
	
	public int getPreferredHeight() {
		return 32;
	}

	@Override
	public void render(Graphics2D g) {
		grid.setWidth(width);
		grid.setHeight(height);
		
		g.setColor(Color.WHITE);
		grid.renderGridlines(g);
		
		for (HeaderColumn headerColumn : headerColumns) {
			headerColumn.headerView.forwardRender(g);
		}
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		for (HeaderColumn headerColumn : headerColumns) {
			InputAction inputAction = headerColumn.headerView.forwardInput(inputType, mousePos, value);
			if (inputAction != null) {
				return inputAction;
			}
		}
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		for (HeaderColumn headerColumn : headerColumns) {
			MouseHoverFeedback mouseHoverFeedback = headerColumn.headerView.applyMouseHover(mousePos);
			if (mouseHoverFeedback != null) {
				return mouseHoverFeedback;
			}
		}
		return null;
	}
}
