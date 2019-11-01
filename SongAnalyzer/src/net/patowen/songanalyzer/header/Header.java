package net.patowen.songanalyzer.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.grid.Grid;
import net.patowen.songanalyzer.grid.GridRow;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.View;

public class Header extends View {
	private final Grid grid;
	private final GridRow gridRow;
	private final List<HeaderColumn> headerColumns = new ArrayList<>();
	
	public Header(RootBundle bundle) {
		grid = new Grid();
		gridRow = new GridRow();
		grid.setCenterRow(gridRow);
		
		headerColumns.add(new HeaderColumn(gridRow, new AudioFileSelector(bundle)));
		headerColumns.add(new HeaderColumn(gridRow, new PlaySpeedInput(bundle)));
		grid.setStartColumns(headerColumns);
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
