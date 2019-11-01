package net.patowen.songanalyzer.header;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.grid.Grid;
import net.patowen.songanalyzer.grid.GridColumn;
import net.patowen.songanalyzer.grid.GridRow;
import net.patowen.songanalyzer.grid.GridSizer;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.View;

public class Header extends View {
	private final Grid grid;
	private final GridRow gridRow;
	private final GridColumn audioFileSelectorColumn = new GridColumn();
	
	private AudioFileSelector audioFileSelector;
	
	public Header(RootBundle bundle) {
		grid = new Grid();
		gridRow = new GridRow();
		grid.setCenterRow(gridRow);
		
		audioFileSelector = new AudioFileSelector(bundle);
		audioFileSelector.setSizer(new GridSizer(audioFileSelectorColumn, gridRow));
		audioFileSelectorColumn.setSize(audioFileSelector.getPreferredWidth());
		
		ArrayList<GridColumn> columnList = new ArrayList<>();
		columnList.add(audioFileSelectorColumn);
		grid.setStartColumns(columnList);
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
		
		audioFileSelector.forwardRender(g);
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return audioFileSelector.forwardInput(inputType, mousePos, value);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return audioFileSelector.applyMouseHover(mousePos);
	}
}
