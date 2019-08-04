package net.patowen.songanalyzer;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class TrackLayer {
	private int height;
	
	public TrackLayer() {
		height = getPreferredHeight();
	}
	
	public final int getHeight() {
		return height;
	}
	
	public final void trySetHeight(int requestedHeight) {
		this.height = requestedHeight;
		if (height < getMinimumHeight()) {
			height = getMinimumHeight();
		}
	}
	
	public abstract void render(Graphics2D g, int width, int height);
	
	public void keyPressed(KeyEvent e) {
	}
	
	public void mousePressed(MouseEvent e, int mouseX, int mouseY) {
	}
	
	public TickerSource getTickerSource() {
		return null;
	}
	
	public abstract int getMinimumHeight();
	public abstract int getPreferredHeight();
}
