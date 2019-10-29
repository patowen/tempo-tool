package net.patowen.songanalyzer.old;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SeekLayer extends TrackLayer {
	private TrackStatus status;
	
	public SeekLayer(TrackStatus status) {
		this.status = status;
	}
	
	@Override
	public void render(Graphics2D g, int width, int height) {
		g.setColor(new Color(128, 128, 128));
		int xLeft = status.bounds.secondsToPixel(0);
		int xRight = status.bounds.secondsToPixel(status.audioStream.getLength());
		g.fillRect(xLeft, 8, xRight - xLeft + 1, height - 16);
	}
	
	@Override
	public void mousePressed(MouseEvent e, int mouseX, int mouseY) {
		status.audioStream.setPos(status.bounds.pixelToSeconds(mouseX));
		status.updatePlayBar();
		status.refresh();
	}
	
	@Override
	public int getMinimumHeight() {
		return 32;
	}
	
	@Override
	public int getPreferredHeight() {
		return 64;
	}
	
	@Override
	public void save(DataOutputStream stream) throws IOException {
		
	}
	
	@Override
	public void load(DataInputStream stream) throws IOException {
		
	}
}
