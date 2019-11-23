package net.patowen.tempotool.header;

import net.patowen.tempotool.grid.GridColumn;
import net.patowen.tempotool.grid.GridRow;
import net.patowen.tempotool.grid.GridSizer;

public class HeaderColumn extends GridColumn {
	public final HeaderView headerView;
	
	public HeaderColumn(GridRow gridRow, HeaderView headerView) {
		this.headerView = headerView;
		this.headerView.setSizer(new GridSizer(this, gridRow));
		trySetSize(this.headerView.getPreferredWidth());
	}
}
