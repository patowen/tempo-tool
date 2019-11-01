package net.patowen.songanalyzer.header;

import net.patowen.songanalyzer.grid.GridColumn;
import net.patowen.songanalyzer.grid.GridRow;
import net.patowen.songanalyzer.grid.GridSizer;

public class HeaderColumn extends GridColumn {
	public final HeaderView headerView;
	
	public HeaderColumn(GridRow gridRow, HeaderView headerView) {
		this.headerView = headerView;
		this.headerView.setSizer(new GridSizer(this, gridRow));
		trySetSize(this.headerView.getPreferredWidth());
	}
}
