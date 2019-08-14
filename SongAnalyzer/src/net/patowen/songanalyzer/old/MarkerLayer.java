package net.patowen.songanalyzer.old;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.TreeSet;

public class MarkerLayer extends TrackLayer {
	private TrackStatus status;
	private TreeSet<Double> marks;
	private int markSelectionRange = 3;
	
	private TickerSource tickerSource;
	
	public MarkerLayer(TrackStatus status) {
		this.status = status;
		marks = new TreeSet<>();
		
		tickerSource = new TickerSource() {
			public Double getNextTickInclusive(double pos) {
				return marks.ceiling(pos);
			}
			
			public Double getNextTickExclusive(double pos) {
				return marks.higher(pos);
			}
		};
	}
	
	public void addMark(double mark) {
		marks.add(mark);
	}
	
	public void removeMark(double mark) {
		marks.remove(mark);
	}
	
	public void addTestMarks() {
		for (int i=0; i<60; i++) {
			marks.add((double)i);
		}
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
				if (!marks.contains(pos)) {
					status.userActionList.applyAction(new UserActionAddMark(this, pos));
					status.refresh();
				}
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
					status.userActionList.applyAction(new UserActionRemoveMark(this, closer));
					status.refresh();
				}
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_N) {
			status.updatePlayBar();
			double mark = status.playBarPos;
			status.userActionList.applyAction(new UserActionAddMark(this, mark));
			status.refresh();
		}
	}
	
	public int getMinimumHeight() {
		return 32;
	}
	
	public int getPreferredHeight() {
		return 64;
	}
	
	public void save(DataOutputStream stream) throws IOException {
		stream.writeInt(marks.size());
		for (double mark : marks) {
			stream.writeDouble(mark);
		}
	}
	
	public void load(DataInputStream stream) throws IOException {
		int numMarks = stream.readInt();
		for (int i=0; i<numMarks; i++) {
			marks.add(stream.readDouble());
		}
	}
}
