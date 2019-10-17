package net.patowen.songanalyzer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputController;

public class SongAnalyzerRunner {
	private Root root;
	
	public SongAnalyzerRunner() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		@SuppressWarnings("serial")
		JPanel panel = new JPanel() {
			public void paint(Graphics g) {
				root.render((Graphics2D) g);
			}
		};
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setFocusable(true);
		
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				root.setWidth(e.getComponent().getWidth());
				root.setHeight(e.getComponent().getHeight());
			}
		});
		
		root = new Root(new UserActionList());
		
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
