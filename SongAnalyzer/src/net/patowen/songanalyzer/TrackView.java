package net.patowen.songanalyzer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TrackView {
	private JPanel trackPanel;
	
	private ArrayList<TrackLayer> layers;
	private TrackLayer activeLayer;
	
	private TrackStatus status;
	private Ticker ticker;
	
	private Timer playBarUpdateTimer;
	
	private int outerBorderWidth = 1, outerBorderHeight = 1, interBorderHeight = 1;
	private int interBorderSelectionRange = 3;
	
	private int trackTabWidth = 8, trackTabBorderWidth = 1;
	
	private boolean resizingLayer;
	private int resizingLayerIndex;
	private int resizingLayerStartHeight;
	private int resizingLayerStartMouseY;
	
	private File defaultFolder;
	private File currentFile;
	
	@SuppressWarnings("serial")
	public TrackView(AudioStream stream) {
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		trackPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				renderPanel((Graphics2D)g);
			}
		};
		trackPanel.setPreferredSize(new Dimension(800, 600));
		trackPanel.setFocusable(true);
		trackPanel.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				TrackView.this.keyPressed(e);
			}
		});
		
		trackPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				TrackView.this.mousePressed(e);
			}
			
			public void mouseReleased(MouseEvent e) {
				TrackView.this.mouseReleased(e);
			}
		});
		
		trackPanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				TrackView.this.mouseMoved(e);
			}
			
			public void mouseDragged(MouseEvent e) {
				TrackView.this.mouseDragged(e);
			}
		});
		
		trackPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				TrackView.this.mouseWheelMoved(e);
			}
		});
		
		playBarUpdateTimer = new Timer(0, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!status.audioStream.isPlaying()) {
					playBarUpdateTimer.stop();
				}
				status.updatePlayBar();
				status.refresh();
			}
		});
		
		ticker = stream.getTicker();

		status = new TrackStatus(trackPanel, stream);
		
		layers = new ArrayList<>();
		layers.add(new SeekLayer(status));
		MarkerLayer markerLayer = new MarkerLayer(status);
		ticker.addSource(markerLayer.getTickerSource());
		layers.add(markerLayer);
		MarkerLayer markerLayer2 = new MarkerLayer(status);
//		ticker.addSource(markerLayer2.getTickerSource());
		markerLayer2.addTestMarks();
		layers.add(markerLayer2);
		
		activeLayer = null;
		
		initialLoad();
		
		
		playBarUpdateTimer.setDelay(10);
		
		resizingLayer = false;
		
		frame.add(trackPanel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void save(DataOutputStream stream) throws IOException {
		stream.writeInt(layers.size());
		for (TrackLayer layer : layers) {
			stream.writeInt(getLayerType(layer));
			layer.save(stream);
		}
	}
	
	public void load(DataInputStream stream) throws IOException {
		layers.clear();
		int numLayers = stream.readInt();
		for (int i=0; i<numLayers; i++) {
			TrackLayer layer = createLayer(stream.readInt());
			layer.load(stream);
		}
	}
	
	public TrackLayer createLayer(int layerType) {
		switch (layerType) {
		case 0: return new SeekLayer(status);
		case 1: return new MarkerLayer(status);
		default: throw new IllegalArgumentException("Invalid layer type: " + layerType);
		}
	}
	
	public int getLayerType(TrackLayer layer) {
		if (layer instanceof SeekLayer) return 0;
		if (layer instanceof MarkerLayer) return 1;
		throw new IllegalArgumentException("Unknown TrackLayer subclass");
	}
	
	public DataOutputStream getDataOutputStream(File file) {
		try {
			DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			return stream;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public DataInputStream getDataInputStream(File file) {
		try {
			DataInputStream stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			return stream;
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public void initialLoad() {
		Preferences prefs = Preferences.userRoot();
		String defaultFolderPath = prefs.get("defaultFolder", null);
		defaultFolder = defaultFolderPath == null ? null : new File(defaultFolderPath);
		String currentFilePath = prefs.get("currentFile", null);
		currentFile = currentFilePath == null ? null : new File(currentFilePath);
		
		if (currentFile != null) {
			DataInputStream stream = getDataInputStream(currentFile);
			if (stream == null) {
				prefs.remove("currentFile");
				currentFile = null;
			} else {
				try {
					load(stream);
				} catch (IOException e) {
					prefs.remove("currentFile");
					currentFile = null;
				}
			}
		}
	}
	
	public void fulfillSaveRequest(boolean forceDialog) {
		File pendingFile = currentFile;
		DataOutputStream stream = null;
		if (!forceDialog && currentFile != null) {
			stream = getDataOutputStream(currentFile);
		}
		
		if (stream == null) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Song Analysis", "sng");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(defaultFolder);
			int returnValue = chooser.showSaveDialog(trackPanel);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File chosenFile = chooser.getSelectedFile();
				if (!chosenFile.exists() && !chosenFile.getName().endsWith(".sng")) {
					chosenFile = new File(chosenFile.getPath() + ".sng");
				}
				if (chosenFile.exists()) {
					int confirmResult = JOptionPane.showConfirmDialog(trackPanel, "Are you sure you want to overwrite " + chosenFile.getName() + "?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
					if (confirmResult != JOptionPane.YES_OPTION) {
						return;
					}
				}
				stream = getDataOutputStream(chosenFile);
				pendingFile = chosenFile;
			} else if (returnValue == JFileChooser.CANCEL_OPTION) {
				return;
			} else if (returnValue == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(trackPanel, "Unexpected error with save dialog", "Unexpected error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		if (stream == null) {
			JOptionPane.showMessageDialog(trackPanel, "Cannot save where you requested. Do you have permission?", "Cannot save", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			save(stream);
			stream.close();
			currentFile = pendingFile;
			Preferences.userRoot().put("currentFile", currentFile.getAbsolutePath());
			defaultFolder = currentFile.getParentFile();
			Preferences.userRoot().put("defaultFolder", defaultFolder.getAbsolutePath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(trackPanel, "Unexpected error occured while saving", "Unexpected error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void fulfillOpenRequest() {
		File pendingFile = currentFile;
		DataInputStream stream = null;
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Song Analysis", "sng");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(defaultFolder);
		int returnValue = chooser.showOpenDialog(trackPanel);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File chosenFile = chooser.getSelectedFile();
			stream = getDataInputStream(chosenFile);
			pendingFile = chosenFile;
		} else if (returnValue == JFileChooser.CANCEL_OPTION) {
			return;
		} else if (returnValue == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(trackPanel, "Unexpected error with open dialog", "Unexpected error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (stream == null) {
			JOptionPane.showMessageDialog(trackPanel, "Cannot open the requested file. Does it exist?", "Cannot open", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			load(stream);
			stream.close();
			currentFile = pendingFile;
			Preferences.userRoot().put("currentFile", currentFile.getAbsolutePath());
			defaultFolder = currentFile.getParentFile();
			Preferences.userRoot().put("defaultFolder", defaultFolder.getAbsolutePath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(trackPanel, "Unexpected error occured while reading file", "Unexpected error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(trackPanel, "File appears to be corrupted", "Cannot load", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void startPlaying() {
		playBarUpdateTimer.restart();
		status.audioStream.play(1);
	}
	
	public void stopPlaying() {
		status.audioStream.pause();
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_1) {
			startPlaying();
		} else if (e.getKeyCode() == KeyEvent.VK_2) {
			stopPlaying();
		} else if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			status.userActionList.undo();
			status.refresh();
		} else if ((e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() && e.isShiftDown() && !e.isAltDown()) || (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown() && !e.isShiftDown() && !e.isAltDown())) {
			status.userActionList.redo();
			status.refresh();
		} else if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() && !e.isAltDown()) {
			fulfillSaveRequest(e.isShiftDown());
		} else if (e.getKeyCode() == KeyEvent.VK_O && e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
			fulfillOpenRequest();
		} else {
			if (activeLayer != null) {
				activeLayer.keyPressed(e);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
		MouseRegion mouseRegion = getMouseRegion(e.getX(), e.getY());
		if (mouseRegion.isLayerBoundary) {
			trackPanel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
			resizingLayer = true;
			resizingLayerIndex = mouseRegion.layerIndex;
			TrackLayer layer = layers.get(resizingLayerIndex);
			resizingLayerStartHeight = layer.getHeight();
			resizingLayerStartMouseY = e.getY();
		}
		if (mouseRegion.isLayer) {
			TrackLayer layer = layers.get(mouseRegion.layerIndex);
			activeLayer = layer;
			status.refresh();
			layer.mousePressed(e, e.getX() - mouseRegion.cornerX, e.getY() - mouseRegion.cornerY);
		}
		if (mouseRegion.isTab) {
			TrackLayer layer = layers.get(mouseRegion.layerIndex);
			activeLayer = layer;
			status.refresh();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		resizingLayer = false;
		MouseRegion mouseRegion = getMouseRegion(e.getX(), e.getY());
		if (mouseRegion.isLayerBoundary) {
			trackPanel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		} else {
			trackPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		MouseRegion mouseRegion = getMouseRegion(e.getX(), e.getY());
		if (mouseRegion.isLayerBoundary || resizingLayer) {
			trackPanel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		} else {
			trackPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if (resizingLayer) {
			trackPanel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
			TrackLayer layer = layers.get(resizingLayerIndex);
			layer.trySetHeight(resizingLayerStartHeight + (e.getY() - resizingLayerStartMouseY));
			status.refresh();
		} else {
			trackPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		double zoomFactor = Math.exp(e.getPreciseWheelRotation() * 0.1);
		status.bounds.zoom(status.bounds.pixelToSeconds(e.getX() - outerBorderWidth), zoomFactor);
		status.refresh();
	}
	
	public void renderPanel(Graphics2D g) {
		g.setColor(Color.BLACK);
		int width = trackPanel.getWidth(), height = trackPanel.getHeight();
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, width-1, height-1);
		
		AffineTransform savedTransform = g.getTransform();
		
		g.translate(outerBorderWidth, outerBorderHeight);
		int innerWidth = width - outerBorderWidth * 2;
		int trackWidth = innerWidth - trackTabWidth - trackTabBorderWidth;
		status.bounds.setWidth(trackWidth);
		for (TrackLayer layer : layers) {
			int layerHeight = layer.getHeight();
			AffineTransform savedTransform2 = g.getTransform();
			g.translate(trackTabWidth + trackTabBorderWidth, 0);
			g.setClip(0, 0, trackWidth, layerHeight);
			layer.render(g, trackWidth, layerHeight);
			g.setTransform(savedTransform2);
			g.setColor(Color.WHITE);
			g.setClip(null);
			g.drawLine(trackTabWidth, 0, trackTabWidth, layerHeight);
			g.drawLine(0, layerHeight, innerWidth, layerHeight);
			
			if (layer == activeLayer) {
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, trackTabWidth, layerHeight);
			}
			
			g.translate(0, layerHeight + interBorderHeight);
		}
		
		g.setTransform(savedTransform);
		g.setColor(Color.GREEN);
		int pos = status.bounds.secondsToPixel(status.playBarPos) + outerBorderWidth + trackTabWidth + trackTabBorderWidth;
		g.drawLine(pos, 0, pos, height);
	}
	
	private class MouseRegion {
		int cornerX;
		int cornerY;
		int layerIndex;
		boolean isLayer;
		boolean isLayerBoundary;
		boolean isTab;
		
		public MouseRegion(int cornerX, int cornerY) {
			this.cornerX = cornerX;
			this.cornerY = cornerY;
			isLayerBoundary = false;
			isLayer = false;
			isTab = false;
		}
	}
	
	private MouseRegion getMouseRegion(int mouseX, int mouseY) {
		int refY = outerBorderHeight;
		if (mouseY < refY) {
			return new MouseRegion(0, 0);
		}
		for (int i = 0; i < layers.size(); i++) {
			TrackLayer layer = layers.get(i);
			if (mouseY - refY < layer.getHeight() - interBorderSelectionRange) {
				if (mouseX >= outerBorderWidth && mouseX < outerBorderWidth + trackTabWidth) {
					MouseRegion mouseRegion = new MouseRegion(outerBorderWidth, refY);
					mouseRegion.isTab = true;
					mouseRegion.layerIndex = i;
					return mouseRegion;
				} else if (mouseX >= outerBorderWidth + trackTabWidth + trackTabBorderWidth && mouseX < trackPanel.getWidth() - outerBorderWidth) {
					MouseRegion mouseRegion = new MouseRegion(outerBorderWidth + trackTabWidth + trackTabBorderWidth, refY);
					mouseRegion.isLayer = true;
					mouseRegion.layerIndex = i;
					return mouseRegion;
				} else {
					return new MouseRegion(0, 0);
				}
			} else if (mouseY - refY < layer.getHeight() + interBorderHeight + interBorderSelectionRange) {
				MouseRegion mouseRegion = new MouseRegion(0, refY);
				mouseRegion.isLayerBoundary = true;
				mouseRegion.layerIndex = i;
				return mouseRegion;
			}
			refY += layer.getHeight() + interBorderHeight;
		}
		return new MouseRegion(0, 0);
	}
}
