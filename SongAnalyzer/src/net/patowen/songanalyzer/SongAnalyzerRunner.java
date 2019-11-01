package net.patowen.songanalyzer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.patowen.songanalyzer.userinput.InputController;

public class SongAnalyzerRunner {
	private JFrame frame;
	private JPanel panel;
	private Root root;
	private RootSizer rootSizer = new RootSizer();
	
	@SuppressWarnings("serial")
	public SongAnalyzerRunner() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO: Are you sure you want to close?
				root.destroy();
				frame.dispose();
			}
		});
		
		panel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				root.forwardRender((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setFocusable(true);
		
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				rootSizer.setSize(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});
		
		root = new Root(panel);
		root.setSizer(rootSizer);
		
		new InputController(panel, root);
		
		frame.add(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new SongAnalyzerRunner();
	}
}
