package net.patowen.songanalyzer.old;

public class TrackLayerWrapper {
	private TrackLayer layer;
	private int height;
	
	public TrackLayerWrapper(TrackLayer layer) {
		this.layer = layer;
		this.height = layer.getPreferredHeight();
	}
	
	public int getHeight() {
		return height;
	}
	
	public void trySetHeight(int requestedHeight) {
		this.height = requestedHeight;
		if (height < layer.getMinimumHeight()) {
			height = layer.getMinimumHeight();
		}
	}
	
	public TrackLayer getTrackLayer() {
		return layer;
	}
}
