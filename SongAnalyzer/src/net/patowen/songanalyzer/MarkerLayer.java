package net.patowen.songanalyzer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.TreeSet;

public class MarkerLayer extends TrackLayer {
	private TrackStatus status;
	private TreeSet<Double> marks;
	private int markSelectionRange = 3;
	
	private TickerSource tickerSource;
	
	public MarkerLayer(TrackStatus status) {
		this.status = status;
		marks = new TreeSet<>();
		for (int i=0; i<60; i++) {
			marks.add((double)i);
		}
		
		tickerSource = new TickerSource() {
			public Double getNextTickInclusive(double pos) {
				return marks.ceiling(pos);
			}
			
			public Double getNextTickExclusive(double pos) {
				return marks.higher(pos);
			}
		};
	}
	
	@Override
	public TickerSource getTickerSource() {
		return tickerSource;
	}
	
	@Override
	public void render(Graphics2D g, int width, int height) {
		g.setColor(new Color(128, 128, 128));
		for (double mark : marks) {
			int pos = status.bounds.secondsToPixel(mark);
			g.drawLine(pos, 0, pos, height);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e, int mouseX, int mouseY) {
		if (e.isShiftDown()) {
			double pos = status.bounds.pixelToSeconds(mouseX);
			if (e.getButton() == MouseEvent.BUTTON1) {
				marks.add(pos);
				status.refresh();
			}
			if (e.getButton() == MouseEvent.BUTTON3 && !marks.isEmpty()) {
				Double lower = marks.floor(pos);
				Double upper = marks.ceiling(pos);
				double closer;
				if (lower == null || (upper != null && upper - pos < pos - lower)) {
					closer = upper;
				} else {
					closer = lower;
				}
				int closerPixel = status.bounds.secondsToPixel(closer);
				if (mouseX >= closerPixel - markSelectionRange && mouseX <= closerPixel + markSelectionRange) {
					marks.remove(closer);
				}
				status.refresh();
			}
		}
	}
	
	public int getMinimumHeight() {
		return 32;
	}
	
	public int getPreferredHeight() {
		return 64;
	}
}
